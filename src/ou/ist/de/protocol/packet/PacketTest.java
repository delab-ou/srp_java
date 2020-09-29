package ou.ist.de.protocol.packet;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;

import ou.ist.de.protocol.Constants;
import ou.ist.de.protocol.routing.dsr.DSRPacket;
import ou.ist.de.protocol.routing.dsr.DSRPacketManager;
import ou.ist.de.protocol.routing.dsr.DSRPacketOption;

public class PacketTest {


	public static void main(String[] args) {
		
		DSRPacketManager dsrpm=new DSRPacketManager();

		Constants.FSIZE=20;
		int loop=10;
		try {
			Header h1=new Header();
			DSRPacketOption o1=new DSRPacketOption();
			h1.dest = InetAddress.getByName("10.0.0.5");
			h1.src= InetAddress.getByName("10.0.0.4");
			h1.sndr= InetAddress.getByName("10.0.0.2");
			h1.next= InetAddress.getByName("10.0.0.2");
			h1.type=0;
			h1.seq=1;
			h1.hops=1;
			o1.getRI().addNode(h1.src);
			for(int i=0;i<loop;i++){
				o1.getRI().addNode(InetAddress.getByName("10.0.0."+String.valueOf(i+20)));
			}

			Header h2=new Header();
			DSRPacketOption o2=new DSRPacketOption();

			h2.dest = InetAddress.getByName("10.0.0.5");
			h2.src= InetAddress.getByName("10.0.0.2");
			h2.sndr= InetAddress.getByName("10.0.0.6");
			h2.next= InetAddress.getByName("10.0.0.255");
			h2.type=0;
			h2.seq=1;
			h2.hops=1;
			o2.getRI().addNode(h1.src);
			for(int i=0;i<loop;i++){
				o2.getRI().addNode(InetAddress.getByName("10.0.0."+String.valueOf(i+30)));
			}

			DSRPacket p1=new DSRPacket(h1,o1);
			DSRPacket p2=new DSRPacket(h2,o2);
			

			System.out.println("p1\n"+p1);
			System.out.println("p2\n"+p2);
			byte[] b=p1.toBytes();
			System.out.println(p1);
			System.out.println("packet byte length is "+b.length);
			DSRPacket p3=new DSRPacket();
			p3.fromBytes(b);
			System.out.println("p3\n"+p3);

			ArrayList<DatagramPacket> alp1=dsrpm.fragment(p1);
			ArrayList<DatagramPacket> alp2=dsrpm.fragment(p2);
			Packet p4=null,p5=null;
		
			int i=0;
			while(p4==null){
				FragmentedPacket fp=new FragmentedPacket();
				fp.fromBytes(alp1.get(i).getData());
				p4=dsrpm.defragment(fp);
				i++;
			}
			i=0;
			while(p5==null){
				FragmentedPacket fp=new FragmentedPacket();
				fp.fromBytes(alp2.get(i).getData());
				p5=dsrpm.defragment(fp);
				i++;
			}
			System.out.println("p4="+p4.toString());
			System.out.println("p5="+p5.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(String s:args) {
			System.out.println(s);
		}
	}

}
