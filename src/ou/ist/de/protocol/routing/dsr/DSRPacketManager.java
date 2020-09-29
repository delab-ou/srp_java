package ou.ist.de.protocol.routing.dsr;

import java.net.InetAddress;

import ou.ist.de.protocol.Constants;
import ou.ist.de.protocol.packet.Header;
import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.packet.PacketManager;

public class DSRPacketManager extends PacketManager {

    public DSRPacketManager(){
        super();
    }
    @Override
    public Packet generateEmptyPacket() {
        return new DSRPacket();
    }

    @Override
    public Packet generateInitialRequestPacket(InetAddress src, InetAddress broadcast, InetAddress dest, int seq) {
        DSRPacket p=(DSRPacket)this.generateEmptyPacket();
        Header h = p.getHeader();
        h.setType(Constants.REQ);
        h.setSrc(src);
        h.setDest(dest);
        h.setSndr(src);
        h.setNext(broadcast);
        h.setHops(1);
        h.setSeq(seq);
        DSRPacketOption o = (DSRPacketOption)p.getOption();
        o.ri.clear();
        o.ri.addNode(src);
        p.setHeader(h);
        p.setOption(o);
        return p;
    }

    @Override
    public Packet generateInitialReplyPacket(Packet p) {
        Header h = p.getHeader();
        DSRPacketOption o = (DSRPacketOption) p.getOption();
        h.setSndr(h.getDest());
        h.setDest(h.getSrc());
        h.setSrc(h.getSndr());
        h.setHops(1);
        h.setType(Constants.REP);
        h.setNext(o.ri.get(o.ri.size() - 1));
        o.ri.addNode(h.getSrc());
        return p;
    }

    @Override
    public Packet generateForwardingRequestPacket(Packet p, InetAddress sndr) {
        DSRPacketOption o = (DSRPacketOption) p.getOption();
        if(o.ri.isContained(sndr)){
            return null;
        }
        Header h = p.getHeader();
        h.setSndr(sndr);
        o.ri.addNode(sndr);
        h.setHops(h.getHops() + 1);
        p.setHeader(h);
        p.setOption(o);
        return p;
    }

    @Override
    public Packet generateForwardingReplyPacket(Packet p, InetAddress sndr) {
        Header h = p.getHeader();
        DSRPacketOption o = (DSRPacketOption) p.getOption();
        if (h.getDest().equals(sndr)) {
            return null;
        }
        if (!o.ri.isContained(sndr)) {
            System.out.println(" not contained");
            return null;
        }
        for (int i = 0; i < o.ri.size(); i++) {
            if (o.ri.get(i).equals(sndr)) {
                h.setNext(o.ri.get(i - 1));
                break;
            }
        }
        h.setSndr(sndr);
        h.setHops(h.getHops() + 1);
        p.setHeader(h);
        p.setOption(o);
        return p;
    }
    
}