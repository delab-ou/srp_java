package ou.ist.de.protocol.utils;

public interface PRF {
	
	public byte[] getRandomBytes(byte[] ... data);
}
