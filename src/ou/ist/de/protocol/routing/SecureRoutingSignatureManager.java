package ou.ist.de.protocol.routing;

import java.util.ArrayList;

import com.herumi.mcl.Mcl;

import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.utils.PRF;
import ou.ist.de.protocol.utils.keys.MCLKey;
import ou.ist.de.protocol.utils.keys.MCLKeyManager;

public abstract class SecureRoutingSignatureManager {

	protected MCLKey preloadKeys;
	protected int curveType;
	protected boolean onPRF;
	protected PRF prf;
	static {
		String lib = "mcljava";
		String libName = System.mapLibraryName(lib);
		System.out.println("libName : " + libName);
		System.loadLibrary(lib);
	}

	public SecureRoutingSignatureManager() {
		this.initializeKey(Mcl.BN254);
	}

	public SecureRoutingSignatureManager(int curveType) {
		this.initializeKey(curveType);
	}

	public abstract boolean verify(Packet p);

	public abstract Packet sign(Packet p);

	public void initializeKey(int curveType) {
		String keyfile = null;
		if (curveType == Mcl.BLS12_381) {
			this.curveType = Mcl.BLS12_381;
			keyfile = "mcl_bls12_381.keys";
		} else if (curveType == Mcl.BN254) {
			this.curveType = Mcl.BN254;
			keyfile = "mcl_bn254.keys";
		} else {
			System.out.println("Curve Type:" + curveType + " is not supported.");
			System.out.println("Curve Type is set as BN254.");
			this.curveType = Mcl.BN254;
			keyfile = "mcl_bn254.keys";
		}

		Mcl.SystemInit(this.curveType);
		ArrayList<MCLKey> alkey = new MCLKeyManager("").importKeys(keyfile);
		preloadKeys = alkey.get(0);
	}
	public void setPRFFlag(boolean flag) {
    	onPRF=flag;
    }
    public void setPRF(PRF prf) {
    	this.prf=prf;
    }
    public PRF getPRF() {
    	return prf;
    }
}
