package ou.ist.de.protocol.packet;

public class FragmentedPacket extends Packet{

    public FragmentedPacket(){
        super();
    }
    public FragmentedPacket(Header h, Option o){
        super(h,o);
    }
    public FragmentedPacket(byte[] b){
        super(b);
    }
    @Override
    public FragmentedOption genOption() {
        return new FragmentedOption();
    }

    @Override
    public Header genHeader() {
        return new Header();
    }

}