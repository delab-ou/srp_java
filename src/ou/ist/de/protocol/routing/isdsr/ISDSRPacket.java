package ou.ist.de.protocol.routing.isdsr;

import java.nio.ByteBuffer;

import com.herumi.mcl.G1;
import com.herumi.mcl.G2;

import ou.ist.de.protocol.packet.Option;
import ou.ist.de.protocol.routing.dsr.DSRPacket;
import ou.ist.de.protocol.routing.dsr.DSRPacketOption;

public class ISDSRPacket extends DSRPacket {

    @Override
    public Option genOption(){
        return new ISDSRPacketOption();
    }
    public class ISDSRPacketOption extends DSRPacketOption{
        protected G1 g1;
        protected G1 g2;
        protected G1 g3g1;
        protected G2 g3g2;

        public ISDSRPacketOption(){
            g1=null;
            g2=null;
            g3g1=null;
            g3g2=null;
        }
        @Override
        public byte[] toBytes(){
            byte[] s=super.toBytes();
            ByteBuffer bb=ByteBuffer.allocate(getByteLength());
            bb.put(s);
            byte[] tmp=null;

            tmp=g1.serialize();
            bb.putInt(tmp.length);
            bb.put(tmp);
            tmp=g2.serialize();
            bb.putInt(tmp.length);
            bb.put(tmp);
            tmp=g3g1.serialize();
            bb.putInt(tmp.length);
            bb.put(tmp);
            tmp=g3g2.serialize();
            bb.putInt(tmp.length);
            bb.put(tmp);

            return bb.array();
        }
        @Override
        public int fromBytes(byte[] b,int offset){
            int pos=super.fromBytes(b, offset);
            ByteBuffer bb=ByteBuffer.wrap(b);
            bb.position(pos);
            int len=0;
            byte[] tmp=null;

            len=bb.getInt();
            tmp=new byte[len];
            bb.get(tmp);
            g1=new G1();
            g1.deserialize(tmp);
            
            len=bb.getInt();
            tmp=new byte[len];
            bb.get(tmp);
            g2=new G1();
            g2.deserialize(tmp);
            
            len=bb.getInt();
            tmp=new byte[len];
            bb.get(tmp);
            g3g1=new G1();
            g3g1.deserialize(tmp);
            
            
            len=bb.getInt();
            tmp=new byte[len];
            bb.get(tmp);
            g3g2=new G2();
            g3g2.deserialize(tmp);

            return bb.position();
        }
        @Override
        public int getByteLength(){
            return super.getByteLength()+g1.serialize().length+g2.serialize().length+g3g1.serialize().length+g3g2.serialize().length+Integer.BYTES*4;
        }
        @Override
        public String toString() {
        	String ret=super.toString()+" g1:"+g1.serialize().length;
        	ret+=" g2:"+g2.serialize().length;
        	ret+=" g3g1:"+g3g1.serialize().length;
        	ret+=" g3g2:"+g3g2.serialize().length;
        	ret+=" total:"+(g1.serialize().length+g2.serialize().length+g3g1.serialize().length+g3g2.serialize().length);
        	return ret;
        }
    }
}