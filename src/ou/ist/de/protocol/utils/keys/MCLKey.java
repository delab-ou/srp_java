package ou.ist.de.protocol.utils.keys;

import com.herumi.mcl.Fr;
import com.herumi.mcl.G1;
import com.herumi.mcl.G2;

public class MCLKey{
	
	protected G1 g1;
	protected G2 g2;
	protected Fr fr1;
	protected Fr fr2;
	
	public G1 getG1() {
		return g1;
	}
	public void setG1(G1 g1) {
		this.g1 = g1;
	}
	public G2 getG2() {
		return g2;
	}
	public void setG2(G2 g2) {
		this.g2 = g2;
	}
	public Fr getFr1() {
		return fr1;
	}
	public void setFr1(Fr fr1) {
		this.fr1 = fr1;
	}
	public Fr getFr2() {
		return fr2;
	}
	public void setFr2(Fr fr2) {
		this.fr2 = fr2;
	}
	
	
}
