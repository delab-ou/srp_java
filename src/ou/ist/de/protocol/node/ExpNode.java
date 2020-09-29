package ou.ist.de.protocol.node;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import ou.ist.de.protocol.Constants;
import ou.ist.de.protocol.packet.Packet;

public class ExpNode extends Node {

	public static int interval_milisec;
	protected int repeat;
	protected HashMap<String, ArrayList<PacketData>> alpd;
	protected InetAddress dest;
	protected long start;

	public ExpNode(HashMap<String, String> params) {
		super(params);
		alpd = new HashMap<String, ArrayList<PacketData>>();
	}

	public void setRepeatTimes(int repeat) {
		this.repeat = repeat;
	}
	public void startRouteEstablishmentRegularRTT(InetAddress dest, long initialInterval) {
		Packet p = null;
		String key = null;
		this.dest = dest;
		ArrayList<PacketData> al = null;
		long rtt=initialInterval;
		long startTime=0;
		for (int i = 0; i < repeat; i++) {
			//p = this.rp.startRouteEstablishment(this.dest);
			p=this.pm.generateInitialRequestPacket(this.addr, this.baddr, dest, seq);
			this.send(p);
			seq++;
			key = "src:" + p.getHeader().getSrc().toString() + ";dest:" + p.getHeader().getDest().toString() + ";seq:" + p.getHeader().getSeq();
			if (!alpd.containsKey(key)) {
				alpd.put(key, new ArrayList<PacketData>());
			}
			al = alpd.get(key);
			startTime=System.currentTimeMillis();
			al.add(new PacketData(p.getHeader().getType(), p.getHeader().getSeq(), startTime, p.getHeader().getHops(),0));
			this.start=System.currentTimeMillis();
			System.out.println(
					"sent to " + p.getHeader().getDest().toString() + " from " + p.getHeader().getSrc().toString() + " seq=" + p.getHeader().getSeq());
			while(al.size()==1) {
				if(System.currentTimeMillis()>(startTime+rtt*2)) {
					break;
				}
			}
			if(al.size()==1) {
				continue;
			}
			else {
				PacketData pd=al.get(1);
				rtt=pd.time-startTime;
				System.out.println("rtt="+rtt);
			}
		}
	}
	public void startRouteEstablishment(InetAddress dest) {
		Packet p = null;
		String key = null;
		this.dest = dest;
		ArrayList<PacketData> al = null;
		for (int i = 0; i < repeat; i++) {
			//p = this.rp.startRouteEstablishment(this.dest);
			p=this.pm.generateInitialRequestPacket(this.addr, this.baddr, dest, seq);
			this.send(p);
			seq++;
			key = "src:" + p.getHeader().getSrc().toString() + ";dest:" + p.getHeader().getDest().toString() + ";seq:" + p.getHeader().getSeq();
			if (!alpd.containsKey(key)) {
				alpd.put(key, new ArrayList<PacketData>());
			}
			al = alpd.get(key);
			al.add(new PacketData(p.getHeader().getType(), p.getHeader().getSeq(), System.currentTimeMillis(), p.getHeader().getHops(),0));
			this.start=System.currentTimeMillis();
			System.out.println(
					"sent to " + p.getHeader().getDest().toString() + " from " + p.getHeader().getSrc().toString() + " seq=" + p.getHeader().getSeq());
			try {
				Thread.sleep(interval_milisec);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void routeEstablished(Packet p) {
		System.out.println("---------- route is established "+p.getHeader().getHops()+" hops seq:"+p.getHeader().getSeq()+"----------");
		String key = "src:" + p.getHeader().getDest().toString() + ";dest:" + p.getHeader().getSrc().toString() + ";seq:" + p.getHeader().getSeq();
		System.out.println(key);
		System.out.println("rtt="+(System.currentTimeMillis()-start));
		if (alpd.containsKey(key)) {
			PacketData pd=alpd.get(key).get(0);
			System.out.println("rtt="+(System.currentTimeMillis()-pd.time));
			alpd.get(key).add(new PacketData(p.getHeader().getType(), p.getHeader().getSeq(), System.currentTimeMillis(), p.getHeader().getHops(),alpd.get(key).size()));
		}
	}

	protected String createTitleRowCSV() {
		return "SRC,DEST,seq,hops,time,rcvs,received\n";
	}
	protected String createRowCSV(PacketData req,PacketData rep,int rcv) {
		String ret=this.addr.toString()+","+this.dest.toString()+","+req.seq+","+rep.hops+","+(rep.time-req.time)+","+rep.cnt+","+rcv+"\n";
		
		return ret;
	}
	protected PacketData findReq(ArrayList<PacketData> al) {
		PacketData pdreq=null;
		for(int j=0;j<al.size();j++) {
			pdreq=al.get(j);
			if(pdreq.type==Constants.REQ) {
				return pdreq;
			}
		}
		return null;
	}
	public void writeResults() {
		System.out.println("writing results");
		String key = null;
		ArrayList<PacketData> al=null;
		int received=0;
		PacketData pdreq=null;
		PacketData pdrep=null;
		long rtt=0;
		
		for(String s:alpd.keySet()) {
			System.out.println("key="+s);
		}
		String csv=this.createTitleRowCSV();
		for (int i = Constants.INIT_SEQ; i < (Constants.INIT_SEQ+this.repeat); i++) {
			System.out.println("i="+i);
			pdreq=null;
			pdrep=null;
			rtt=0;
			key = "src:" + this.addr.toString() + ";dest:" + this.dest.toString() + ";seq:" + i;
			al=this.alpd.get(key);
			if(al.size()==1) {
				//System.out.println("no route established");
				continue;
			}
			received++;
			pdreq=this.findReq(al);
			if(pdreq==null) {
				//System.out.println("no request seq:"+i);
				System.exit(1);
			}
			for(int j=0;j<al.size();j++) {
				pdrep=al.get(j);
				if(pdrep.type==Constants.REP) {
					rtt+=(pdrep.time-pdreq.time);
					csv+=this.createRowCSV(pdreq, pdrep,received);
				}
			}
		}
		System.out.println(csv);

		String filename = params.get("-protocol")+"_"+params.get(Constants.ARG_SIG_ALGO)+"_"+params.get(Constants.ARG_SIG_TYPE)+dest.toString().split("\\.")[3]+".csv";
		this.writeData(csv,filename);
	}
	protected void writeData(String contents,String filename) {
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File(filename)));
			bw.write(contents);
			bw.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public class PacketData {
		protected int type;
		protected int seq;
		protected long time;
		protected int hops;
		protected int cnt;

		public PacketData(int type, int s, long t, int hops,int cnt) {
			this.type = type;
			this.seq = s;
			this.time = t;
			this.hops = hops;
			this.cnt=cnt;
		}
	}
}
