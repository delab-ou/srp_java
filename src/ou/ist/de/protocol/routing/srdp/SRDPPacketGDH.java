package ou.ist.de.protocol.routing.srdp;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.herumi.mcl.G1;
import com.herumi.mcl.G2;

import ou.ist.de.protocol.packet.Option;
import ou.ist.de.protocol.routing.dsr.DSRPacket;
import ou.ist.de.protocol.routing.dsr.DSRPacketOption;

public class SRDPPacketGDH extends DSRPacket{

	
	@Override
	public Option genOption() {
		// TODO Auto-generated method stub
		return new SRDPPacketOptionGDH();
	}

	public class SRDPPacketOptionGDH extends DSRPacketOption{
		protected G1 sig;
		protected ArrayList<G2> alpubs;
		public SRDPPacketOptionGDH() {
			super();
			sig=null;
			alpubs=new ArrayList<G2>();
		}
		@Override
		public byte[] toBytes() {
			ByteBuffer bb=null;
			bb=ByteBuffer.allocate(getByteLength());
			byte[] b=super.toBytes();
			bb.put(b);
			if(sig==null) {
				bb.putInt(0);
				return bb.array();
			}
			byte[] sb=sig.serialize();
			bb.putInt(sb.length);
			bb.put(sb);
			bb.putInt(alpubs.size());
			byte[] pubtmp=null;
			for(int i=0;i<alpubs.size();i++) {
				pubtmp=alpubs.get(i).serialize();
				bb.putInt(pubtmp.length);
				bb.put(pubtmp);
			}
			
			return bb.array();
		}
		@Override
		public int fromBytes(byte[] b, int offset) {
			// TODO Auto-generated method stub
			int pos=super.fromBytes(b,offset);
			ByteBuffer bb=ByteBuffer.wrap(b);
			bb.position(pos);
			int len=bb.getInt();
			if(len==0) {
				return bb.position();
			}
			byte[] sb=new byte[len];
			bb.get(sb);
			sig=new G1();
			sig.deserialize(sb);
			
			len=bb.getInt();
			alpubs.clear();
			byte[] pubtmp=null;
			for(int i=0;i<len;i++) {
				pubtmp=new byte[bb.getInt()];
				bb.get(pubtmp);
				G2 pub=new G2();
				pub.deserialize(pubtmp);
				alpubs.add(pub);
			}
			return bb.position();
		}
		@Override
		public void copy(Option o) {
			// TODO Auto-generated method stub
			super.copy(o);
			byte[] sb=((SRDPPacketOptionGDH)o).sig.serialize();
			if(sig==null) {
				sig=new G1();
			}
			sig.deserialize(sb);
			alpubs.clear();
			
			for(int i=0;i<((SRDPPacketOptionGDH)o).alpubs.size();i++) {
				G2 pub=new G2();
				pub.deserialize(((SRDPPacketOptionGDH)o).alpubs.get(i).serialize());
				alpubs.add(pub);
			}
		}
		@Override
		public int getByteLength() {
			// TODO Auto-generated method stub
			int ret=super.getByteLength();
			if(sig==null) {
				return ret+Integer.BYTES;
			}
			ret+=Integer.BYTES*2;// for sig and the length of alpubs;
			ret+=sig.serialize().length;
			ret+=alpubs.size()*Integer.BYTES;
			for(G2 g2:alpubs) {
				ret+=g2.serialize().length;
			}
			return ret;
		}
		@Override
		public String toString() {
			String ret="sig bit length:"+((sig==null)?"null":sig.serialize().length);
			int cnt=0;
			for(G2 g2:alpubs) {
				cnt+=g2.serialize().length;
			}
			return ret+" pubkey length:"+cnt;
		}
		
	}
}
