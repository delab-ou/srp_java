package ou.ist.de.protocol.routing.dsr;

import ou.ist.de.protocol.packet.Header;
import ou.ist.de.protocol.packet.Option;
import ou.ist.de.protocol.packet.Packet;

public class DSRPacket extends Packet{

    public DSRPacket(){
        super();
    }
    public DSRPacket(Header h,Option o){
        super(h,o);
    }
    @Override
    public Option genOption() {
        return new DSRPacketOption();
    }

    @Override
    public Header genHeader() {
        return new Header();
    }
}