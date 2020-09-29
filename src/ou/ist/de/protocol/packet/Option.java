package ou.ist.de.protocol.packet;


public abstract class Option {
    public abstract void copy(Option o);
    public abstract byte[] toBytes();
    public abstract int fromBytes(byte[] b,int offset);
    public abstract int getByteLength();
}