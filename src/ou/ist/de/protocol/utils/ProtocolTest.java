package ou.ist.de.protocol.utils;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import ou.ist.de.protocol.Constants;
import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.packet.PacketManager;
import ou.ist.de.protocol.routing.PacketManagerFactory;
import ou.ist.de.protocol.routing.ProtocolType;
public class ProtocolTest {
	
	protected RuntimeArgs argvalue;
	protected HashMap<String,String> params;
	protected int protoid;
	public ProtocolTest() {
		argvalue=new RuntimeArgs();
		params=new HashMap<String,String>();
	}
	public static void main(String args[]) {
		ProtocolTest pt=new ProtocolTest();
		pt.argvalue.setArgs(args, pt.params);
		pt.runTest(pt.params);
	}
	
	public PacketManager generatePacketManager(HashMap<String,String> params) {
		int id=0;
		if(params.containsKey(Constants.ARG_CONFIG_PROTO_SETTING)) {
			id=Integer.valueOf(params.get(Constants.ARG_CONFIG_PROTO_SETTING));
		}
		else {
			System.out.println("-config-protoid is needed");
			System.exit(0);
		}
		
		return PacketManagerFactory.generatePacketManager(ProtocolType.correspondingType(id));
	}
	
	
	public void runTest(HashMap<String, String> params) {
		ArrayList<PacketManager> alpm = generatePM(params);
		ArrayList<InetAddress> aladdr = generateAddress(alpm.size());
		
		long sigtime = 0;
		long veritime = 0;
		long t = System.currentTimeMillis();
		Packet p = alpm.get(0).generateInitialRequestPacket(aladdr.get(0), aladdr.get(aladdr.size() - 1),
				aladdr.get(aladdr.size() - 2), 1);
		byte[] tmp = p.toBytes();
		for (int i = 1; i < alpm.size() - 1; i++) {
			//System.out.println("num:" + i + " addr:" + aladdr.get(i));
			Packet rcv = alpm.get(i).generateEmptyPacket();
			rcv.fromBytes(tmp);
			p = alpm.get(i).generateForwardingRequestPacket(p, aladdr.get(i));
			//System.out.println("request " + p);
			tmp = p.toBytes();
		}
		sigtime = System.currentTimeMillis() - t;
		System.out.println("signing time:" + sigtime);
		t = System.currentTimeMillis();
		p = alpm.get(alpm.size() - 1).generateInitialReplyPacket(p);
		tmp = p.toBytes();
		for (int i = alpm.size() - 2; i >= 0; i--) {
			Packet rcv = alpm.get(i).generateEmptyPacket();
			rcv.fromBytes(tmp);
			p = alpm.get(i).generateForwardingReplyPacket(rcv, aladdr.get(i));
			//System.out.println("addr=" + aladdr.get(i));
			//System.out.println(p);
			tmp = (p == null) ? null : p.toBytes();
		}
		veritime = System.currentTimeMillis() - t;

		System.out.println("signing time:" + sigtime);
		System.out.println("verification time:" + veritime);
		//System.out.println("packet=" + p);
	}
	public ArrayList<PacketManager> generatePM(HashMap<String, String> params) {
		ArrayList<PacketManager> al = new ArrayList<PacketManager>();
		int repeat=(params.containsKey(Constants.ARG_REPEAT))?Integer.valueOf(params.get(Constants.ARG_REPEAT)):10;
		for(int i=0;i<repeat;i++) {
			al.add(generatePacketManager(params));
		}
		return al;
	}
	public ArrayList<InetAddress> generateAddress(int num) {
		ArrayList<InetAddress> al = new ArrayList<InetAddress>();
		try {
			for (int i = 0; i < num; i++) {
				al.add(InetAddress.getByName("10.0.0." + String.valueOf(i + 20)));
			}
			al.add(InetAddress.getByName("10.0.0.255"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
}
