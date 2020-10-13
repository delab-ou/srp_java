package ou.ist.de.protocol.packet;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import ou.ist.de.protocol.Constants;

public abstract class PacketManager {
    protected HashMap<String,ArrayList<FragmentedPacket>> reqFPs;
    protected HashMap<String,ArrayList<FragmentedPacket>> repFPs;
    protected ArrayList<DatagramPacket> aldp;
    protected ArrayList<FragmentedPacket> alfp;
    protected int currentReqSeq;
    protected int currentRepSeq;
    
	
    public PacketManager(){
        this.reqFPs=new HashMap<String,ArrayList<FragmentedPacket>>();
        this.repFPs=new HashMap<String,ArrayList<FragmentedPacket>>();
        this.aldp=new ArrayList<DatagramPacket>();
        this.alfp=new ArrayList<FragmentedPacket>();
        currentReqSeq=0;
        currentRepSeq=0;
    }
    public abstract Packet generateEmptyPacket();
    public Packet generatePacket(byte[] b) {
    	Packet p=generateEmptyPacket();
    	p.fromBytes(b);
    	return p;
    }
    public abstract Packet generateInitialRequestPacket(InetAddress src, InetAddress broadcast,InetAddress dest, int seq);
    public abstract Packet generateInitialReplyPacket(Packet p);
    public abstract Packet generateForwardingRequestPacket(Packet p,InetAddress sndr);
    public abstract Packet generateForwardingReplyPacket(Packet p, InetAddress sndr);
    public Packet generateInitialErrorPacket() {return null;}
    public Packet generateForwardingErrorPacket() {return null;}

    public ArrayList<DatagramPacket> fragment(Packet p){
        byte[] bo=p.option.toBytes();
        aldp.clear();
        alfp.clear();
        DatagramPacket dp=null;
		int num=bo.length/Constants.FSIZE+((bo.length%Constants.FSIZE)!=0?1:0);
		int s=0,e=0;
        FragmentedOption fo=null;
        Header fh=null;
		if(num==0) {
            
            fh=new Header();
            fh.copy(p.header);
            fo=new FragmentedOption();
			fo.totalLength=1;
			fo.totalCount=1;
			fo.index=1;
			fo.fragmented=null;
			FragmentedPacket fp=new FragmentedPacket(fh,fo);
			alfp.add(fp);
		}
		else {
			for(int i=0;i<num;i++) {
                fh=new Header();
                fh.copy(p.header);
                fo=new FragmentedOption();
				fo.totalLength=bo.length;
				fo.totalCount=num;
				fo.index=i;
				s=e;
				e=e+Constants.FSIZE;
				if(e>=bo.length) {
					e=bo.length;
				}
				fo.fragmented=new byte[(e-s)];
				for(int j=0;j<(e-s);j++) {
					fo.fragmented[j]=bo[s+j];
                }
                
				FragmentedPacket fp=new FragmentedPacket(fh,fo);
				System.out.println("fragmented "+i+" "+fp.toString());
				alfp.add(fp);
			}
        }
        for (FragmentedPacket fp : alfp) {
            byte[] data = fp.toBytes();
            //System.out.println("next:"+fp.getHeader().getNext());
            dp = new DatagramPacket(data, data.length, fp.getHeader().getNext(), Constants.PORT);
            aldp.add(dp);
        }
        return aldp;
    }
    protected Packet defragment(FragmentedPacket fp,HashMap<String,ArrayList<FragmentedPacket>> fps) {

        FragmentedOption fo=(FragmentedOption)fp.option;
        String h=fp.header.headerInformation()+"totalLength:"+fo.totalLength+" totalCount:"+fo.totalCount;
		System.out.println("defragment key is "+h);
		System.out.println("received index is "+fo.index);
        ArrayList<FragmentedPacket> aldeffp=null;
        Packet p=null;
        //if(fo.totalLength==1) {
		//	return translate(fp);
		//}
        if(fo.totalCount==1) {
			return translate(fp);
		}
		
		if(fps.containsKey(h)) {
			aldeffp=fps.get(h);
		}
		else {
			aldeffp=new ArrayList<FragmentedPacket>();
			fps.put(h, aldeffp);
		}
		boolean dup=false;
		for(int i=0;i<aldeffp.size();i++) {
			//System.out.println("stored fp["+i+"] index="+((FragmentedOption)aldeffp.get(i).option).index);
			if(fo.index==((FragmentedOption)aldeffp.get(i).option).index) {
				dup=true;
			}
		}
		if(!dup) {
			aldeffp.add(fp);
			//System.out.println("fp is added index="+((FragmentedOption)fp.option).index);
		}
		//System.out.println("aldeffp size = "+aldeffp.size());
		if(aldeffp.size()==fo.totalCount) {
            p=this.generateEmptyPacket();
            aldeffp.sort((a,b)-> ((FragmentedOption)a.option).index-((FragmentedOption)b.option).index);
			ByteBuffer bb=ByteBuffer.allocate(fo.totalLength);
			p.header.copy(fp.header);
			for(int i=0;i<aldeffp.size();i++) {
				bb.put(((FragmentedOption)aldeffp.get(i).option).fragmented);
            }
            p.option.fromBytes(bb.array(), 0);
            aldeffp.clear();
            fps.remove(h);
		}
		
		return p;
    }
    public Packet defragment(FragmentedPacket fp){
    	int fpseq=fp.header.seq;
    	
    	if(fp.header.type==Constants.REQ) {
    		if(currentReqSeq<fpseq) {
        		//System.out.println("seq is larger current seq="+currentSeq+" received seq="+fp.header.seq);
        		currentReqSeq=fpseq;
        		reqFPs.clear();
        	}
    		if(currentReqSeq > fpseq) {
        		//System.out.println("seq is less current seq="+currentSeq+" received seq="+fp.header.seq);
        		return null;
        	}
    		return defragment(fp,reqFPs);
    	}
    	
    	if(fp.header.type==Constants.REP) {
    		if(currentRepSeq<fpseq) {
    			currentRepSeq=fpseq;
    			repFPs.clear();
    		}
    		if(currentRepSeq > fpseq) {
    			return null;
    		}
    		return defragment(fp,repFPs);
    	}
    	return null;
    }
    protected  Packet translate(FragmentedPacket fp) {
		Packet p=this.generateEmptyPacket();
        p.header.copy(fp.header);
        byte[] b=new byte[((FragmentedOption)fp.option).totalLength];
		for(int i=0;i<b.length;i++) {
			b[i]=((FragmentedOption)fp.option).fragmented[i];
        }
        p.option.fromBytes(b, 0);
		return p;
	}
}