package ou.ist.de.protocol.routing.srp;

import java.math.BigInteger;

import com.herumi.mcl.Fr;
import com.herumi.mcl.G1;
import com.herumi.mcl.Mcl;

import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.routing.SecureRoutingSignatureManager;
import ou.ist.de.protocol.routing.srp.SRPECDSAPacket.SRPECDSAPacketOption;
import ou.ist.de.protocol.utils.HashCalc;

public class SRPSignatureManager extends SecureRoutingSignatureManager{

	protected G1 pubkey;
	protected G1 g;
	protected Fr sec;
	protected BigInteger r;
	protected BigInteger p;
	
	public SRPSignatureManager() {
		super();
	}
	public SRPSignatureManager(int curveType) {
		super(curveType);
	}

	@Override
	public boolean verify(Packet p) {
		SRPECDSAPacketOption o=(SRPECDSAPacketOption)p.getOption();
		SRPECDSAPacketOption tmpo=new SRPECDSAPacket().new SRPECDSAPacketOption();
		//System.out.println("start verifying");
		
		G1 tmpp=null;
		Fr tmps=null;
		Fr tmpr=null;
		boolean ret=true;
		for(int i=0;i<o.getRI().size();i++) {
			tmpo.getRI().addNode(o.getRI().get(i));
			tmpp=o.alpubkey.get(i);
			tmps=o.alsigs.get(i);
			tmpr=o.alsigr.get(i);
			byte[] h=null;
			if(tmpo.alpubkey.size()==0) {
				h=HashCalc.hash(tmpo.getRI().planeByteArray(),tmpp.serialize());
			}
			else {
				h=HashCalc.hash(tmpo.getRI().planeByteArray(),tmpp.serialize(),tmpo.getSigByteArray());
			}
			
			G1 hm=new G1();
			Mcl.hashAndMapToG1(hm, h);
			//String[] hmparam=hm.toString(16).split(" ");
			//BigInteger bihmx=new BigInteger(hmparam[1],16);
			//Fr hmxfr=new Fr(bihmx.mod(r).toString());
			Fr hmxfr=new Fr(new BigInteger(hm.toString().split(" ")[1]).mod(r).toString());
			
			Fr hms=new Fr();
			Mcl.div(hms, hmxfr, tmps);
			G1 lsg=new G1();
			Mcl.mul(lsg, g, hms);
			
			Fr rs=new Fr();
			Mcl.div(rs, tmpr, tmps);
			G1 rsg=new G1();
			Mcl.mul(rsg, tmpp, rs);
			G1 result=new G1();
			Mcl.add(result, lsg, rsg);
			
			//String[] rslt=result.toString(16).split(" ");
			//BigInteger birslt=new BigInteger(rslt[1],16);
			//Fr re=new Fr(birslt.mod(r).toString());
			Fr re=new Fr(new BigInteger(result.toString().split(" ")[1]).mod(r).toString());
			ret=ret&& (tmpr.equals(re));
            if(!ret) {
                System.out.println("------ verification false -----"+i);
                return false;
            }
            else {
            	System.out.println("------ verification true -----"+i);
            }
            tmpo.alpubkey.add(tmpp);
            tmpo.alsigr.add(tmpr);
            tmpo.alsigs.add(tmps);
		}
		return ret;
	}

	@Override
	public Packet sign(Packet p) {
		SRPECDSAPacketOption o=(SRPECDSAPacketOption)p.getOption();
		//System.out.println("start signing");
		byte[] h=null;
		if(o.alpubkey.size()==0) {
			h=HashCalc.hash(o.getRI().planeByteArray(),pubkey.serialize());
		}
		else {
			h=HashCalc.hash(o.getRI().planeByteArray(),pubkey.serialize(),o.getSigByteArray());
		}
		Fr k=new Fr();
		k.setByCSPRNG();
		G1 kg=new G1();
		Mcl.mul(kg, g, k);
		//String[] kgparam=kg.toString(16).split(" ");
		//BigInteger bisigr=new BigInteger(kgparam[1],16);
		//Fr sigr=new Fr(bisigr.mod(r).toString()); 
		Fr sigr=new Fr(new BigInteger(kg.toString().split(" ")[1]).mod(r).toString());
		
		G1 hm=new G1();
		Mcl.hashAndMapToG1(hm, h);
		//String[] hmparam=hm.toString(16).split(" ");
		//BigInteger bihmx=new BigInteger(hmparam[1],16);
		//Fr hmxfr=new Fr(bihmx.mod(r).toString());
		Fr hmxfr =new Fr(new BigInteger(hm.toString().split(" ")[1]).mod(r).toString());
		Fr xr =new Fr();
		Mcl.mul(xr, sigr, sec);
		Fr hmxr=new Fr();
		Mcl.add(hmxr, hmxfr, xr);;
		Fr sigs=new Fr();
		Mcl.div(sigs, hmxr, k);
		o.alpubkey.add(pubkey);
		o.alsigr.add(sigr);
		o.alsigs.add(sigs);
		return p;
	}
	protected void setSecretKey() {
		sec=new Fr();
        sec.setByCSPRNG();
	}
	@Override
	public void initializeKey(int curveType) {
		super.initializeKey(curveType);
		if(this.curveType==Mcl.BLS12_381) {
			r=new BigInteger("73eda753299d7d483339d80809a1d80553bda402fffe5bfeffffffff00000001",16);
			p=new BigInteger("1a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaaab",16);
		}
		if(this.curveType==Mcl.BN254) {
			r=new BigInteger("2523648240000001ba344d8000000007ff9f800000000010a10000000000000d",16);
			p=new BigInteger("2523648240000001ba344d80000000086121000000000013a700000000000013",16);
		}
        g=preloadKeys.getG1();
        pubkey=new G1();
        setSecretKey();
        Mcl.mul(pubkey, g, sec);
        
	}

}
