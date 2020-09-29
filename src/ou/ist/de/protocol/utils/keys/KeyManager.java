package ou.ist.de.protocol.utils.keys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
public abstract class KeyManager<T>{

    protected KeyManager(){

    }
    public abstract T genKey();
    public abstract byte[] serializeKey(T key);
    public abstract T deserializeKey(byte[] b);

    public void exportKeys(String filename, int numOfKeys){
        try {
            System.out.println("key generation");
            ArrayList<byte[]> alkeys=new ArrayList<byte[]>();
            for(int i=0;i<numOfKeys;i++){
                alkeys.add(serializeKey(genKey()));
            }
            File f = new File(filename);
			ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(f));
            System.out.println("start writing");
            oos.writeObject(alkeys);
			oos.close();
			} catch (Exception e) {
			return;
		}
    }
    public ArrayList<T> importKeys(String filename){
        try {
            System.out.println("filename="+filename);
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
            ArrayList<byte[]> alkeys=(ArrayList<byte[]>)ois.readObject();
            ois.close();
            ArrayList<T> ret=new ArrayList<T>();
            for(byte[] b:alkeys) {
            	ret.add(deserializeKey(b));
            }
            return ret;			
		} catch (Exception e) {
            e.printStackTrace();
            System.out.println("error at import keys from "+filename);
            System.exit(1);
        }
        return null;
    }
    
    public byte[] generateByteArray(byte[] ... ba) {
    	int total=0;
    	for(int i=0;i<ba.length;i++) {
    		total+=ba[i].length;
    	}
    	ByteBuffer bb=ByteBuffer.allocate(total+Integer.BYTES*ba.length);
    	for(int i=0;i<ba.length;i++) {
    		bb.putInt(ba[i].length);
    		bb.put(ba[i]);
    	}
    	return bb.array();
    	
    }
}