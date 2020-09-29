package ou.ist.de.protocol.utils;

public class HashPRF implements PRF {

	@Override
	public byte[] getRandomBytes(byte[] ... data) {	
		return HashCalc.hash(data);
	}

}
