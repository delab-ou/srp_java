package ou.ist.de.protocol.utils.keys;

import java.nio.ByteBuffer;
import java.util.Random;

import com.herumi.mcl.Fr;
import com.herumi.mcl.G1;
import com.herumi.mcl.G2;
import com.herumi.mcl.Mcl;


public class MCLKeyManager extends KeyManager<MCLKey> {
    static {
        String lib = "mcljava";
        String libName = System.mapLibraryName(lib);
        System.out.println("libName : " + libName);
        System.loadLibrary(lib);
	}
    protected String paramFile;

    public MCLKeyManager(String paramFile){
        this.paramFile=paramFile;
    }
    public void setParamFile(String paramFile){
        this.paramFile=paramFile;
    }
    public String getParamFile(){
        return this.paramFile;
    }
    @Override
    public MCLKey genKey() {
        int param=Mcl.BN254;
		if(this.paramFile.equalsIgnoreCase("bls12_381")) {
			param=Mcl.BLS12_381;
		}
		else if(this.paramFile.equalsIgnoreCase("bn254")) {
			param=Mcl.BN254;
		}
		else {
			System.out.println("parameter "+paramFile+" is not supported");
			return null;
        }
        Mcl.SystemInit(param); // curveType = Mcl.BN254 or Mcl.BLS12_381
        MCLKey key=new MCLKey();
		
		Random rnd = new Random();	
		key.g1=new G1();
		key.g2=new G2();

        String rndv=String.valueOf(rnd.nextInt());
        Mcl.hashAndMapToG1(key.g1, rndv.getBytes());
        Mcl.hashAndMapToG2(key.g2, rndv.getBytes());

		key.fr1=new Fr();
        key.fr1.setByCSPRNG();
        key.fr2=new Fr();
        key.fr2.setByCSPRNG();
        return key;
    }
	@Override
	public byte[] serializeKey(MCLKey key) {
		return generateByteArray(key.g1.serialize(),key.g2.serialize(),key.fr1.serialize(),key.fr2.serialize());
	}
	@Override
	public MCLKey deserializeKey(byte[] b) {
		MCLKey key=new MCLKey();
		ByteBuffer bb=ByteBuffer.wrap(b);
		byte[] tmp;
		tmp=new byte[bb.getInt()];
		bb.get(tmp);
		key.g1=new G1();
		key.g1.deserialize(tmp);
		tmp=new byte[bb.getInt()];
		bb.get(tmp);
		key.g2=new G2();
		key.g2.deserialize(tmp);
		tmp=new byte[bb.getInt()];
		bb.get(tmp);
		key.fr1=new Fr();
		key.fr1.deserialize(tmp);
		tmp=new byte[bb.getInt()];
		key.fr2=new Fr();
		key.fr2.deserialize(tmp);
		return key;
	}
    
}