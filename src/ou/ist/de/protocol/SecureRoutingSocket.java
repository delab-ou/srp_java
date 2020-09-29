package ou.ist.de.protocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

import ou.ist.de.protocol.packet.FragmentedPacket;
import ou.ist.de.protocol.packet.Header;
import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.packet.PacketManager;
import ou.ist.de.protocol.routing.PacketManagerFactory;
import ou.ist.de.protocol.routing.ProtocolType;

public class SecureRoutingSocket {
	
	protected DatagramSocket ds;

	protected InetAddress addr;
	protected InetAddress baddr;
	protected PacketManager pm;
	
	
	
	public SecureRoutingSocket() {
		this(Constants.PORT);
	}
	public SecureRoutingSocket(int port) {
		this(ProtocolType.ISDSR_BN254,port);
	}
	public SecureRoutingSocket(ProtocolType type,int port) {
		Constants.PORT=port;
		pm=PacketManagerFactory.generatePacketManager(type);
	}
	

	protected void initializeDatagramSocket() {
		try {
			// dsR = new DatagramSocket(this.port);
			System.out.println("initialize datagram socket");
			// ds=new DatagramSocket(port,addr);
			ds = new DatagramSocket(Constants.PORT);
			ds.setBroadcast(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void initializeAddress(int[] prefix) {
		try {
			Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
			if (ifs == null) {
				return;
			} else {
				while (ifs.hasMoreElements()) {
					NetworkInterface ni = ifs.nextElement();
					for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
						InetAddress addr = ia.getAddress();
						byte[] b = addr.getAddress();
						boolean check = true;
						for (int i = 0; i < prefix.length; i++) {
							check &= ((b[i] & 0x00FF) == prefix[i]);
						}
						if (check) {
							this.addr = addr;
							this.baddr = ia.getBroadcast();
							byte[] ba = this.baddr.getAddress();
							if ((ba[0] == 0) && (ba[1] == 0) && (ba[2] == 0) && (ba[3] == 0)) {
								ba[0] = (byte) addr.getAddress()[0];
								ba[1] = (byte) 0xff;
								ba[2] = (byte) 0xff;
								ba[3] = (byte) 0xff;
								this.baddr = InetAddress.getByAddress(ba);
							}
							return;
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(DatagramPacket dp) {
		
	}
	public void receive(DatagramPacket dp) {
		
	}
	public void send(Packet p) {
		// System.out.println("In Sender sending packet is "+p.toString());
		ArrayList<DatagramPacket> aldp = pm.fragment(p);
		try {
			for (DatagramPacket dp : aldp) {
				//System.out.println("send fragmented " + dp);
				ds.send(dp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	protected void receivePacket(Packet p) {
		Header h = p.getHeader();
		Packet pkt = null;
		long time = System.currentTimeMillis();
		// System.out.println("receive from "+p.getSndr()+" seq:"+p.getSeq()+"
		// hops:"+p.getHops()+" type:"+p.getType());
		if (h.getDest().equals(this.addr)) {
			if (h.getType() == Constants.REQ) {
				pkt = pm.generateInitialReplyPacket(p);
			} else {
				pkt = pm.generateForwardingReplyPacket(p, this.addr);
				this.routeEstablished(p);
			}
		} else {
			Long t = null;
			if (h.getType() == Constants.REQ) {
				if (h.getSndr().equals(this.addr)) {
					return;
				}
				if (h.getSrc().equals(this.addr)) {
					return;
				}
				String reqCache = "" + h.getType() + h.getSrc().toString() + h.getDest().toString() + h.getSeq();
				t = Long.valueOf(System.currentTimeMillis());
				if (cache.containsKey(reqCache)) {
					if ((t - cache.get(reqCache)) < Constants.TIMEOUT) {
						// System.out.println("cached");
						return;
					}
				}
				cache.put(reqCache, t);
				pkt = pm.generateForwardingRequestPacket(p, this.addr);
			} else if (h.getType() == Constants.REP) {
				pkt = pm.generateForwardingReplyPacket(p, this.addr);
			} else {
				return;
			}
		}
		if (pkt != null) {
			//System.out.println("In RoutingProtocol receivedPacket send " + pkt.toString());
			this.send(pkt);
			System.out.println("received packet consumes " + (System.currentTimeMillis() - time) + " milisec");
		}
	}
	public class Receiver implements Runnable {

		protected boolean loop;

		public void setLoopTrue() {
			this.loop = true;
		}

		public void setLoopFalse() {
			this.loop = false;
		}

		@Override
		public void run() {
			DatagramPacket dp = null;
			byte[] data = new byte[Constants.RCVBUFFER];
			
			try {
				dp = new DatagramPacket(data, Constants.RCVBUFFER);
				while (loop) {
					for (int i = 0; i < Constants.RCVBUFFER; i++) {
						data[i] = 0;
					}
					dp.setData(data);
					// System.out.println("In Receiver run start receiving");
					// dp = new DatagramPacket(new byte[Constants.RCVBUFFER], Constants.RCVBUFFER);

					ds.receive(dp);
					FragmentedPacket fp = new FragmentedPacket();
					fp.fromBytes(dp.getData());
					Packet p = pm.defragment(fp);

					if (p == null) {
						continue;
					} else {
						receivePacket(p);
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
}
