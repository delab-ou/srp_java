package ou.ist.de.protocol.packet;

import java.nio.ByteBuffer;

public abstract class Packet{
	protected Header header;
	protected Option option;
	public Packet() {
		header=genHeader();
		option=genOption();
    }
    public Packet(Header h,Option o){
        this.header=h;
        this.option=o;
    }
	
	public Packet(Packet p) {
		this();
		copy(p);
	}
	public Packet(byte[] b){
		this();
		this.fromBytes(b);
	}
	public abstract Option genOption();
	public abstract Header genHeader();
	@Override
	public String toString() {
		String ret = header.toString();
		
		if(option ==null) {
			ret+=" opt:null\n";
		}
		else {
			ret+=option.toString();
			ret+="]\n";
		}
		return ret;
	}
	public int getSize() {
		return header.getByteLength()+option.getByteLength();
	}

	public byte[] toBytes() {
		
        ByteBuffer bb = ByteBuffer.allocate(this.getSize());
        //System.out.println("packet header:"+header.toBytes().length+" opt:"+option.toBytes().length);
		bb.put(this.header.toBytes());
		bb.put(this.option.toBytes());
		return bb.array();
	}
	public int fromBytes(byte[] b){
		int pos=this.header.fromBytes(b, 0);
		pos=this.option.fromBytes(b, pos);
		return pos;
	}
	public void copy(Packet p){
		this.header.copy(p.header);
		this.option.copy(p.option);
	}

	public Header getHeader(){
		return header;
	}
	public void setHeader(Header header){
		this.header=header;
	}
	public Option getOption() {
		return option;
	}

	public void setOption(Option option) {
		this.option = option;
	}
}
