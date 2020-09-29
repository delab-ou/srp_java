package ou.ist.de.protocol.routing.srdp;

import java.net.InetAddress;

import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.routing.SecureRoutingPacketManager;
import ou.ist.de.protocol.routing.SecureRoutingSignatureManager;

public class SRDPPacketManager extends SecureRoutingPacketManager{

	public SRDPPacketManager() {
		super();
	}
	public SRDPPacketManager(int curveType) {
		super(curveType);
	}
	@Override
	protected SecureRoutingSignatureManager generateSignatureManager(int curveType) {
		return new SRDPSignatureManager(curveType);
	}
	@Override
    public Packet generateEmptyPacket() {
		return new SRDPPacketGDH();
	}
	@Override
	public Packet generateInitialRequestPacket(InetAddress src, InetAddress broadcast, InetAddress dest, int seq) {
		// TODO Auto-generated method stub
		return super.generateInitialRequestPacket(src, broadcast, dest, seq);
	}

	@Override
	public Packet generateInitialReplyPacket(Packet p) {
		// TODO Auto-generated method stub
		Packet pkt=super.generateInitialReplyPacket(p);
		 pkt=sm.sign(pkt);
	     return pkt;
	}

	@Override
	public Packet generateForwardingRequestPacket(Packet p, InetAddress sndr) {
		// TODO Auto-generated method stub
		return super.generateForwardingRequestPacket(p, sndr);
	}

	@Override
	public Packet generateForwardingReplyPacket(Packet p, InetAddress sndr) {
		// TODO Auto-generated method stub
		if(verifyall){
            boolean v=sm.verify(p);
            System.out.println("verify="+v);
            if(!v){
                return null;
            }
        }
		if(p.getHeader().getDest().equals(sndr)) {
			System.out.println("verify: "+sm.verify(p));
		}
        Packet pkt=super.generateForwardingReplyPacket(p, sndr);
       // System.out.println("pkt="+pkt);
        if(pkt==null){
            return null;
        }
        pkt=sm.sign(pkt);
        return pkt;
	}
}
