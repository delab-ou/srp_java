package ou.ist.de.protocol.routing.srdp;

import java.math.BigInteger;

import com.herumi.mcl.Fr;
import com.herumi.mcl.G1;
import com.herumi.mcl.G2;
import com.herumi.mcl.GT;
import com.herumi.mcl.Mcl;

import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.routing.SecureRoutingSignatureManager;
import ou.ist.de.protocol.routing.dsr.RouteInfo;
import ou.ist.de.protocol.routing.srdp.SRDPPacketGDH.SRDPPacketOptionGDH;
import ou.ist.de.protocol.utils.HashCalc;

public class SRDPSignatureManager extends SecureRoutingSignatureManager{
	protected G2 global;
	protected G2 pub;
	protected Fr sec;
		
	public SRDPSignatureManager() {
		super();
	}
	public SRDPSignatureManager(int curveType) {
		super(curveType);
	}
	public Packet sign(Packet p) {
		SRDPPacketOptionGDH o=(SRDPPacketOptionGDH)p.getOption();

		RouteInfo ri=o.getRI();
		byte[] h=HashCalc.hash(ri.toBytes());
		System.out.println("signing hash = "+new BigInteger(h));
		
		G1 tmps=new G1();
		Mcl.hashAndMapToG1(tmps, h);
		Mcl.mul(tmps,tmps,sec);
		if(o.sig==null) {
			o.sig=tmps;
		}
		else {
			Mcl.add(o.sig,o.sig,tmps);
		}
		o.alpubs.add(pub);
		p.setOption(o);
		return p;
	}
	public boolean verify(Packet p) {
		SRDPPacketOptionGDH o=(SRDPPacketOptionGDH)p.getOption();
		
		RouteInfo ri=o.getRI();
		byte[] h=HashCalc.hash(ri.toBytes());
		System.out.println("verifying hash = "+new BigInteger(h));
		
		G1 hg1=new G1();
		Mcl.hashAndMapToG1(hg1, h);
		G2 pubs=null;
		
		if(o.alpubs.size()==1) {
			pubs=o.alpubs.get(0);
		}
		else {
			pubs=new G2();
			Mcl.add(pubs, o.alpubs.get(0), o.alpubs.get(1));
		}
		for(int i=2;i<o.alpubs.size();i++) {
			Mcl.add(pubs, pubs, o.alpubs.get(i));
		}
		
		GT gt1=new GT();
        GT gt2=new GT();
        Mcl.pairing(gt1,hg1,pubs );
		Mcl.pairing(gt2,o.sig,global);
		return gt1.equals(gt2);
	}
	@Override
	public void initializeKey(int curveType) {
		super.initializeKey(curveType);
        global=preloadKeys.getG2();
        sec=new Fr();
        pub=new G2();
        sec.setByCSPRNG();
        Mcl.mul(pub,global,sec);
    }
}
