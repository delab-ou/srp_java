package ou.ist.de.protocol.routing.srp;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.herumi.mcl.Fr;
import com.herumi.mcl.G1;

import ou.ist.de.protocol.packet.Header;
import ou.ist.de.protocol.packet.Option;
import ou.ist.de.protocol.routing.dsr.DSRPacket;
import ou.ist.de.protocol.routing.dsr.DSRPacketOption;

public class SRPECDSAPacket extends DSRPacket{

	
	public SRPECDSAPacket() {
		super();
	}
	public SRPECDSAPacket(Header h, Option o) {
		super(h,o);
	}
	
	@Override
	public Option genOption() {
		return new SRPECDSAPacketOption();
	}

	public class SRPECDSAPacketOption extends DSRPacketOption{
		ArrayList<G1> alpubkey;
		ArrayList<Fr> alsigr;
		ArrayList<Fr> alsigs;
		
		public SRPECDSAPacketOption() {
			alpubkey=new ArrayList<G1>();
			alsigr=new ArrayList<Fr>();
			alsigs=new ArrayList<Fr>();
		}
		public byte[] getSigByteArray(){
            int total=Integer.BYTES;
            
            for(int i=0;i<alpubkey.size();i++){
            	total+=Integer.BYTES*3;
                total+=alpubkey.get(i).serialize().length;
                total+=alsigr.get(i).serialize().length;
                total+=alsigs.get(i).serialize().length;
            }
            ByteBuffer bb=ByteBuffer.allocate(total);
            byte[] tmp;
            bb.putInt(alpubkey.size());
            for(int i=0;i<alpubkey.size();i++){
            	tmp=alpubkey.get(i).serialize();
				bb.putInt(tmp.length);
				bb.put(tmp);
				tmp=alsigr.get(i).serialize();
				bb.putInt(tmp.length);
				bb.put(tmp);
				tmp=alsigs.get(i).serialize();
				bb.putInt(tmp.length);
				bb.put(tmp);
            }
            return bb.array();
        }
		@Override
		public byte[] toBytes() {
			// TODO Auto-generated method stub
			byte[] b=super.toBytes();
			ByteBuffer bb=ByteBuffer.allocate(getByteLength());
			bb.put(b);
			bb.put(getSigByteArray());
			return bb.array();
		}
		@Override
		public int fromBytes(byte[] b, int offset) {
			int pos=super.fromBytes(b, offset);
			ByteBuffer bb=ByteBuffer.wrap(b);
			bb.position(pos);
			alpubkey.clear();
			alsigr.clear();
			alsigs.clear();
			int len=bb.getInt();
			byte[] tmp=null;
			for(int i=0;i<len;i++) {
				tmp=new byte[bb.getInt()];
				bb.get(tmp);
				G1 pub=new G1();
				pub.deserialize(tmp);
				alpubkey.add(pub);
				tmp=new byte[bb.getInt()];
				bb.get(tmp);
				Fr r = new Fr();
				r.deserialize(tmp);
				alsigr.add(r);
				tmp=new byte[bb.getInt()];
				bb.get(tmp);
				Fr s = new Fr();
				s.deserialize(tmp);
				alsigs.add(s);
			}
			return bb.position();
		}
		@Override
		public void copy(Option o) {
			// TODO Auto-generated method stub
			super.copy(o);
			
		}
		@Override
		public int getByteLength() {
			int ret=super.getByteLength();
			ret+=Integer.BYTES;
			for(int i=0;i<alpubkey.size();i++) {
				ret+=Integer.BYTES*3;
				ret+=alpubkey.get(i).serialize().length;
				ret+=alsigr.get(i).serialize().length;
				ret+=alsigs.get(i).serialize().length;
			}
			return ret;
		}
		@Override
		public String toString() {
			String ret="";
			int cnt=0;
			for(G1 g1:alpubkey) {
				cnt+=g1.serialize().length;
			}
			ret+="sig bit length:"+cnt;
			cnt=0;
			for(Fr f:alsigr) {
				cnt+=f.serialize().length;
			}
			ret+=" sigr:"+cnt;
			cnt=0;
			for(Fr f:alsigs) {
				cnt+=f.serialize().length;
			}
			return ret+" sigs:"+cnt;
		}
	}
}
