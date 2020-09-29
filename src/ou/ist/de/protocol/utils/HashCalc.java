package ou.ist.de.protocol.utils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class HashCalc {

    public static byte[] hash(byte[] ... ba){
        int total=0;
        ByteBuffer bb=null;
        byte[] ret=null;
        if(ba.length==1){
            ret=ba[0];
        }
        else {
            for(int i=0;i<ba.length;i++){
                total+=ba[i].length;
            }
            bb=ByteBuffer.allocate(total);
            for(int i=0;i<ba.length;i++){
                bb.put(ba[i]);
            }
            ret=bb.array();
        }
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.update(ret);

            BigInteger tmp = new BigInteger(sha.digest());
            if (tmp.compareTo(BigInteger.ZERO) < 0) {
                tmp = tmp.negate();
            }
            ret = tmp.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}