package ou.ist.de.protocol.routing.isdsr;

import java.net.InetAddress;

import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.routing.SecureRoutingPacketManager;
import ou.ist.de.protocol.routing.SecureRoutingSignatureManager;

public class ISDSRPacketManager extends SecureRoutingPacketManager {

	
	public ISDSRPacketManager() {
		super();
	}
	public ISDSRPacketManager(int curveType) {
		super(curveType);
	}
    @Override
    public Packet generateEmptyPacket() {
    	return new ISDSRPacket();
    }

    @Override
    public Packet generateInitialRequestPacket(InetAddress src, InetAddress broadcast, InetAddress dest, int seq) {
        Packet p=super.generateInitialRequestPacket(src,broadcast,dest,seq);
        p=sm.sign(p);
        return p;
    }

    @Override
    public Packet generateInitialReplyPacket(Packet p) {
        boolean v=sm.verify(p);
        System.out.println("verify="+v);
        if(!v){
            return null;
        }
        Packet pkt=super.generateInitialReplyPacket(p);
        pkt=sm.sign(pkt);
        return pkt;
    }

    @Override
    public Packet generateForwardingRequestPacket(Packet p, InetAddress sndr) {
        if(verifyall){
            boolean v=sm.verify(p);
            //System.out.println("verify="+v);
            if(!v){
                return null;
            }
        }
        Packet pkt=super.generateForwardingRequestPacket(p, sndr);
        if(pkt==null){
            return null;
        }
        pkt=sm.sign(pkt);
        return pkt;
    }

    @Override
    public Packet generateForwardingReplyPacket(Packet p, InetAddress sndr) {
        if(verifyall){
            boolean v=sm.verify(p);
            //System.out.println("isdsr packet manager verify="+v);
            if(!v){
                return null;
            }
        }
        Packet pkt=super.generateForwardingReplyPacket(p, sndr);
        System.out.println("isdsr packet manager forwarding reply pkt="+pkt);
        if(pkt==null){
            return null;
        }
        return pkt;
    }

	@Override
	protected SecureRoutingSignatureManager generateSignatureManager(int curveType) {
		return new ISDSRSignatureManager(curveType);
	}
}