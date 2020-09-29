package ou.ist.de.protocol.utils.keys;

public class MasterKeyGen{

    public static void main(String[] args){
        
        MCLKeyManager mcl=new MCLKeyManager("bls12_381");
        mcl.exportKeys("mcl_bls12_381.keys", 10);
        mcl.setParamFile("bn254");
        mcl.exportKeys("mcl_bn254.keys", 10);
        System.out.println("finish mcl key generation");
        
        
    }
}