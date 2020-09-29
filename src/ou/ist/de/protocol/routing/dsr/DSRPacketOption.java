package ou.ist.de.protocol.routing.dsr;

import ou.ist.de.protocol.packet.Option;

public class DSRPacketOption extends Option{
    protected RouteInfo ri;

    public DSRPacketOption(){
        ri=new RouteInfo();
    }
    @Override
    public byte[] toBytes() {
        return ri.toBytes();
    }

    @Override
    public int fromBytes(byte[] b, int offset) {
        return ri.fromBytes(b, offset);
    }

    @Override
    public void copy(Option o) {
        this.ri.copy(((DSRPacketOption)o).ri);
    }

    @Override
    public int getByteLength() {
        return ri.getByteLength();
    }
    public RouteInfo getRI(){
        return ri;
    }
    public void setRI(RouteInfo ri){
        this.ri=ri;
    }
    public String toString(){
        return ri.toString();
    }
}