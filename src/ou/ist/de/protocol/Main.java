package ou.ist.de.protocol;

import java.net.InetAddress;
import java.util.HashMap;

import ou.ist.de.protocol.node.ExpNode;
import ou.ist.de.protocol.node.Node;
import ou.ist.de.protocol.packet.PacketManager;
import ou.ist.de.protocol.routing.PacketManagerFactory;
import ou.ist.de.protocol.routing.ProtocolType;
import ou.ist.de.protocol.utils.RuntimeArgs;

public class Main {

	protected HashMap<String, String> params;
	protected RuntimeArgs avs;

	public Main() {
		params = new HashMap<String, String>();
		avs = new RuntimeArgs();
	}

	public Main(String[] args) {
		this();
		avs.setArgs(args,params);
	}

	public HashMap<String, String> getParameters() {
		return params;
	}

	public String getParameter(String key) {
		return params.get(key);
	}

	public PacketManager getPacketManager() {
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
	public void run() {

		Node node = new Node(params);

		String protocol = this.getParameter("-protocol");
		System.out.println("Protocol: " + protocol);
		PacketManager pm = this.getPacketManager();
		if (pm == null) {
			System.out.println("No protocol was set or the protocol name was wrong.");
			System.exit(0);
		}
		node.setPacketManager(pm);
		node.start();
		if (params.containsKey("-dest")) {
			try {
				node.startRouteEstablishment(InetAddress.getByName(params.get("-dest")));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void runRepeatMesureRegularRTT() {
		ExpNode.interval_milisec = Integer.valueOf(params.get(Constants.ARG_INTERVAL));
		ExpNode node = new ExpNode(params);
		String protocol = this.getParameter("-protocol");
		System.out.println("Protocol: " + protocol);
		PacketManager pm = this.getPacketManager();
		node.setRepeatTimes(Integer.valueOf(params.get("-repeat")));
		node.setPacketManager(pm);
		node.start();

		if (params.containsKey("-dest")) {
			try {
				node.startRouteEstablishmentRegularRTT(InetAddress.getByName(params.get("-dest")), 10000);
				// Thread.sleep(600000);
				node.writeResults();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void runRepeat() {
		ExpNode.interval_milisec = Integer.valueOf(params.get(Constants.ARG_INTERVAL));
		ExpNode node = new ExpNode(params);
		String protocol = this.getParameter("-protocol");
		System.out.println("Protocol: " + protocol);
		PacketManager pm = this.getPacketManager();
		node.setRepeatTimes(Integer.valueOf(params.get("-repeat")));
		node.setPacketManager(pm);
		node.start();

		if (params.containsKey("-dest")) {
			try {
				node.startRouteEstablishment(InetAddress.getByName(params.get("-dest")));
				Thread.sleep(300000);
				node.writeResults();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	

	public static void main(String[] args) {
		Main m = new Main(args);

		// java ou.ist.de.protocol.Main -protocol:DSR -port:000 -dest:10.0.0.0
		// -frag:1000
		if (args.length < 2) {
			System.out.println(
					"usage java ou.ist.de.protocol.Main -protocol:{DSR|ISDSR|SRDP|SRP|ARAN|AODV} -port:portnum -frag:size of fragmentation -dest:destination ip");
			System.exit(0);
		}
		if (m.params.containsKey("-dest")) {
			if (m.params.containsKey(Constants.ARG_MEASURE_REGULAR_RTT)) {
				m.runRepeatMesureRegularRTT();
			} else {
				m.runRepeat();
			}
		} else {
			m.run();
		}

	}


}
