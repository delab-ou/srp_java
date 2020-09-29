package ou.ist.de.protocol.routing.isdsr;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import com.herumi.mcl.Fr;
import com.herumi.mcl.G1;
import com.herumi.mcl.G2;
import com.herumi.mcl.GT;
import com.herumi.mcl.Mcl;

import ou.ist.de.protocol.packet.Header;
import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.routing.SecureRoutingSignatureManager;
import ou.ist.de.protocol.routing.isdsr.ISDSRPacket.ISDSRPacketOption;
import ou.ist.de.protocol.utils.HashCalc;


public class ISDSRSignatureManager extends SecureRoutingSignatureManager {

    protected String uid;
    protected ISDSRKeys key;
    static {
        String lib = "mcljava";
        String libName = System.mapLibraryName(lib);
        System.out.println("libName : " + libName);
        System.loadLibrary(lib);
	}
    public ISDSRSignatureManager() {
        super(Mcl.BN254);
    }
    public ISDSRSignatureManager(int curveType) {
        super(curveType);
        this.setup();
        this.keyDerivation();
    }

    public void setup() {

        key=new ISDSRKeys();
            
        key.mpk1g1=preloadKeys.getG1();
        key.mpk1g2=preloadKeys.getG2();
        key.msk1=preloadKeys.getFr2();
        key.msk2=preloadKeys.getFr2();

        key.mpk2=new G2();
        Mcl.mul(key.mpk2, key.mpk1g2, key.msk1);
        key.mpk3=new G2();
        Mcl.mul(key.mpk3, key.mpk1g2, key.msk2);
    }

    public void keyDerivation() {
        // System.out.println("key derivation uid=" + uid);
        key.isk1=H1(uid);
        key.isk2=H2(uid);

		Mcl.mul(key.isk1, key.isk1, key.msk1);
		Mcl.mul(key.isk2, key.isk2, key.msk2);
    }

    @Override
    public boolean verify(Packet p) {
        boolean ret = false;
        ISDSRPacketOption o=(ISDSRPacketOption)p.getOption();
		System.out.println("start verification");
		String[] uid = o.getRI().getAddrArray();
		String msg = "";
		int num = o.getRI().size();

		GT t1 = new GT();
		GT t2 = new GT();
		Mcl.pairing(t1, o.g1, key.mpk1g2);
		Mcl.pairing(t2, o.g2, o.g3g2);

		String m1 = msg;

		G1 t3=new G1();
		t3.clear();
		G1 t7=new G1();
		t7.clear();
		G1 t4=null,t5=null;
		Fr t6=null;
		for (int i = 0; i < num; i++) {
			t4= H2(uid[i]);// Hash2(uid[i].getBytes());
			Mcl.add(t3,t3,t4);
			t5 = H1(uid[i]);// Hash1(uidByte);
			m1 = m1 + uid[i];
			t6 = H3((uid[i] + m1));// Hash3(m2.getBytes());
			Mcl.mul(t5,t5,t6);
			Mcl.add(t7,t7,t5);
		}
		GT t8 =new GT();
		GT t9=new GT();
		Mcl.pairing(t8, t3, key.mpk3);
		Mcl.pairing(t9,t7,key.mpk2);
		
		Mcl.mul(t2, t2, t8);
		Mcl.mul(t2,t2,t9);
		
		
		if (t1.equals(t2)) {
			ret = true;
        } 
        System.out.println("verification "+ret);
		return ret;
    }

    @Override
    public Packet sign(Packet p) {
        Header h=p.getHeader();
        ISDSRPacketOption o=(ISDSRPacketOption)p.getOption();

        if(uid==null){
            uid=h.getSndr().toString();
            keyDerivation();
        }
        String uid=o.getRI().get(o.getRI().size()-1).toString();
		String msg=o.getRI().getAddrSequence();
        
        if(o.g1==null){
            o.g1=new G1();
            o.g1.clear();
            o.g2=new G1();
            o.g2.clear();
            o.g3g1=new G1();
            o.g3g1.clear();
            o.g3g2=new G2();
            o.g3g2.clear();
        }
        Fr r = new Fr();
		Fr x = new Fr();
		if(this.onPRF) {
			byte[] htmp=msg.getBytes();
			BigInteger btmp=new BigInteger(prf.getRandomBytes(htmp)).mod(new BigInteger(String.valueOf(Long.MAX_VALUE)));
			System.out.println("prf "+btmp);
			r.setStr(btmp.abs().toString());
			x.setStr(btmp.abs().toString());
		}
		else {
			r.setByCSPRNG();
			x.setByCSPRNG();
		}
		G1 newsig1 = new G1();
		G1 newsig2 = new G1();
		G1 newsig3g1 = new G1();
		G2 newsig3g2 = new G2();

		Mcl.mul(newsig3g1, key.mpk1g1, x); // newsig3g1=xg
		Mcl.mul(newsig3g2, key.mpk1g2, x); // newsig3g2=xg
		Mcl.add(newsig3g1, newsig3g1, o.g3g1);// newsig3g1=xg+sig3g1 sig3' is done
		Mcl.add(newsig3g2, newsig3g2, o.g3g2);// newsig3g2=xg+sig3g2 sig3' is done
		Mcl.mul(newsig2, key.mpk1g1, r); // newsig2=rg
		Mcl.add(newsig2, newsig2, o.g2); // newsig2=rg+sig2 sig2' is done

		G1 tmp = new G1();
		Mcl.mul(tmp, o.g3g1, r);// r*sig3
		Mcl.add(newsig1, o.g1, tmp);// newsig1 = r*sig+sig1
		Mcl.mul(tmp, newsig2, x);// tmp = sig2'*x
		Mcl.add(newsig1, newsig1, tmp);// newsig1=newsig1 + sig2' * x
		Mcl.add(newsig1, newsig1, key.isk2);// tmpsigs[0]=tmpsigs[0]+sk2,sk2:alpha2*H2(ID)
		Fr hash = H3(uid + msg);// H3(ID || m)
		Mcl.mul(tmp, key.isk1, hash);// sk1 * H3(ID||m), sk1:alpha1*H1(ID)
		Mcl.add(newsig1, newsig1, tmp);

        o.g1=newsig1;
        o.g2=newsig2;
        o.g3g1=newsig3g1;
        o.g3g2=newsig3g2;
		System.out.println("total siglength="+(o.g1.serialize().length+o.g2.serialize().length+o.g3g1.serialize().length+o.g3g2.serialize().length));
		//System.out.println("verification = " + this.verify(ri, ret));

		return p;
    }


	protected G1 H1(String str) {
		G1 ret = new G1();
		Mcl.hashAndMapToG1(ret, str.getBytes());
		return ret;

	}

	protected G1 H2(String str) {
		G1 ret = new G1();
		byte[] src = HashCalc.hash(str.getBytes());
		Mcl.hashAndMapToG1(ret, src);
		return ret;
	}

	protected Fr H3(String str) {
		byte[] digest=HashCalc.hash(str.getBytes());
		ByteBuffer bb=ByteBuffer.wrap(digest);
		int hash=bb.getInt();
		if(hash<0){
			hash=hash*(-1);
		}
		
		//Fr ret = new Fr(hash.toString());
		Fr ret=new Fr(hash);
		return ret;
    }
    public class ISDSRKeys{
        protected G1 mpk1g1;
        protected G2 mpk1g2;
        protected G2 mpk2;
        protected G2 mpk3;
        
        protected Fr msk1;
        protected Fr msk2;
        
        protected G1 isk1;
        protected G1 isk2;
    }
}