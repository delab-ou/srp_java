package ou.ist.de.protocol.packet;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import ou.ist.de.protocol.Constants;

public class Header{
    protected byte type;
	protected int seq;
	protected InetAddress src;
	protected InetAddress dest;
	protected InetAddress sndr;
	protected InetAddress next;
	protected int hops;

	public Header() {
		type = 0;
		seq = 0;
		src = null;
		dest = null;
		sndr = null;
		next = null;
		hops = 0;
	}

	public Header(byte type, int seq, InetAddress src, InetAddress dest, InetAddress sndr, InetAddress next,
			int hops) {
		this.type = type;
		this.seq = seq;
		this.src = src;
		this.dest = dest;
		this.sndr = sndr;
		this.next = next;
		this.hops = hops;
	}

	public Header(byte[] b) {
		this.fromBytes(b, 0);
	}
	
	public int getByteLength() {
		if (src == null || dest == null || sndr == null || next == null) {
			return 0;
		}
		int ret = 0;
		ret = src.getAddress().length + dest.getAddress().length + sndr.getAddress().length + next.getAddress().length;
		ret += Byte.BYTES;// type 1
		ret += Integer.BYTES;// seq 4
		ret += Integer.BYTES;// hops 4
		return ret;
	}

	public byte[] toBytes() {

        ByteBuffer bb = ByteBuffer.allocate(this.getByteLength());
        bb.put(this.type);
		bb.putInt(this.seq);
		bb.put(this.src.getAddress());
		bb.put(this.dest.getAddress());
		bb.put(this.sndr.getAddress());
		bb.put(this.next.getAddress());
		bb.putInt(this.hops);
		return bb.array();
    }
    
    public int fromBytes(byte[] b, int offset) {
        ByteBuffer bb = ByteBuffer.wrap(b);
        
        try{
            this.type=bb.get();
            this.seq=bb.getInt();
            byte[] addr=new byte[Constants.InetAddressLength];
            bb.get(addr);
            this.src=InetAddress.getByAddress(addr);
            bb.get(addr);
            this.dest=InetAddress.getByAddress(addr);
            bb.get(addr);
            this.sndr=InetAddress.getByAddress(addr);
            bb.get(addr);
            this.next=InetAddress.getByAddress(addr);
            this.hops=bb.getInt();
            return bb.position();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return 0;
    }
	public void copy(Header h) {
		this.type=h.type;
		this.seq=h.seq;
		this.src=h.src;
		this.dest=h.dest;
		this.sndr=h.sndr;
		this.next=h.next;
		this.hops=h.hops;
	}
	public String headerInformation() {
		String ret = "type:" + type;
		ret += " src:" + src.toString();
		ret += " dest:" + dest.toString();
		ret += " next:" + ((next == null) ? "null" : next.toString());
		ret += " sndr:" + ((sndr == null) ? "null" : sndr.toString());
		ret += " hops:" + hops + " seq:" + seq + "\n";
		return ret;
	}
	public String toString() {
		return headerInformation();
	}
	public boolean equals(Header h) {
		if(this.type!=h.type) {
			return false;
		}
		if(this.seq!=h.seq) {
			return false;
		}
		if(this.hops!=h.hops) {
			return false;
		}
		if(!this.src.equals(h.src)) {
			return false;
		}
		if(!this.dest.equals(h.dest)) {
			return false;
		}
		if(!this.next.equals(h.next)) {
			return false;
		}
		if(!this.sndr.equals(h.sndr)) {
			return false;
		}
		return true;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public InetAddress getSrc() {
		return src;
	}

	public void setSrc(InetAddress src) {
		this.src = src;
	}

	public InetAddress getDest() {
		return dest;
	}

	public void setDest(InetAddress dest) {
		this.dest = dest;
	}

	public InetAddress getSndr() {
		return sndr;
	}

	public void setSndr(InetAddress sndr) {
		this.sndr = sndr;
	}

	public InetAddress getNext() {
		return next;
	}

	public void setNext(InetAddress next) {
		this.next = next;
	}

	public int getHops() {
		return hops;
	}

	public void setHops(int hops) {
		this.hops = hops;
	}
}