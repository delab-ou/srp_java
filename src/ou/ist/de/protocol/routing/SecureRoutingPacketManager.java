package ou.ist.de.protocol.routing;


import com.herumi.mcl.Mcl;

import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.routing.dsr.DSRPacketManager;
import ou.ist.de.protocol.utils.PRF;
public abstract class SecureRoutingPacketManager extends DSRPacketManager{

	protected SecureRoutingSignatureManager sm;
	protected boolean verifyall;
	
	public SecureRoutingPacketManager(){
		this(Mcl.BN254);
	}
	public SecureRoutingPacketManager(int curveType) {
		sm=this.generateSignatureManager(curveType);
	}
	protected abstract SecureRoutingSignatureManager generateSignatureManager(int curveType);
    @Override
	public abstract Packet generateEmptyPacket();
	public boolean getPRFFlag() {
    	return sm.onPRF;
    }
    public void setPRFFlag(boolean flag) {
    	sm.setPRFFlag(flag);
    }
    public void setPRF(PRF prf) {
    	sm.setPRF(prf);
    }
    public PRF getPRF() {
    	return sm.getPRF();
    }
    public void setVerifyAllFlag(boolean f) {
    	verifyall=f;
    }
}
