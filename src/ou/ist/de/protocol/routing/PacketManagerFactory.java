package ou.ist.de.protocol.routing;

import com.herumi.mcl.Mcl;

import ou.ist.de.protocol.routing.isdsr.ISDSRPacketManager;
import ou.ist.de.protocol.routing.srdp.SRDPPacketManager;
import ou.ist.de.protocol.routing.srp.SRPPacketManager;

public class PacketManagerFactory {
	public static SecureRoutingPacketManager generatePacketManager(ProtocolType type) {
		SecureRoutingPacketManager pm=null;
		switch(type) {
		case ISDSR_BN254:{
			pm=new ISDSRPacketManager(Mcl.BN254);
			break;
		}
		case ISDSR_BLS12:{
			pm=new ISDSRPacketManager(Mcl.BLS12_381);
			break;
		}
		case SRDP_BN254:{
			pm=new SRDPPacketManager(Mcl.BN254);
			break;
		}
		case SRDP_BLS12:{
			pm=new SRDPPacketManager(Mcl.BLS12_381);
			break;
		}
		case SRP_BN254:{
			pm=new SRPPacketManager(Mcl.BN254);
			break;
		}
		case SRP_BLS12:{
			pm=new SRPPacketManager(Mcl.BLS12_381);
			break;
		}
		default:{
			
		}
		}
		return pm;
	}
}
