package ou.ist.de.protocol.packet;

import java.nio.ByteBuffer;

public class FragmentedOption extends Option{
    protected int totalLength;
	protected int totalCount;
	protected int index;
    protected byte[] fragmented;
    
    @Override
    public byte[] toBytes() {
        ByteBuffer bb=ByteBuffer.allocate(this.getByteLength());
        bb.putInt(this.totalLength);
        bb.putInt(this.totalCount);
        bb.putInt(this.index);
        if(fragmented==null){
            bb.putInt(0);
        }
        else{
            bb.putInt(this.fragmented.length);
            bb.put(this.fragmented);
        }
        return bb.array();
    }

    @Override
    public int fromBytes(byte[] b, int offset) {
        ByteBuffer bb=ByteBuffer.wrap(b);
        bb.position(offset);
        this.totalLength=bb.getInt();
        this.totalCount=bb.getInt();
        this.index=bb.getInt();
        int len=bb.getInt();
        if(len==0){
            this.fragmented=null;
        }
        else{
            this.fragmented=new byte[len];
            bb.get(this.fragmented);
        }
        return bb.position();
    }

    @Override
    public void copy(Option o) {
        FragmentedOption fo=(FragmentedOption)o;
        this.totalLength=fo.totalLength;
        this.totalCount=fo.totalCount;
        this.index=fo.index;
        this.fragmented=fo.fragmented;
    }

    @Override
    public int getByteLength() {
        return Integer.BYTES*4+fragmented.length;
    }
    public String toString(){
        return "total len="+this.totalLength+" total cnt="+this.totalCount+" index:"+this.index+" fragment len="+this.fragmented.length;
    }
}