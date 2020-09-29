package ou.ist.de.protocol.routing;

public enum ProtocolType {
	ISDSR_BN254,ISDSR_BLS12,SRDP_BN254,SRDP_BLS12,SRP_BN254,SRP_BLS12;
	public static ProtocolType correspondingType(int id) {
		switch(id) {
		case 101:{
			return ProtocolType.ISDSR_BLS12;
		}
		case 102:{
			return ProtocolType.ISDSR_BN254;
		}
		case 201:{
			return ProtocolType.SRDP_BLS12;
		}
		case 202:{
			return ProtocolType.SRDP_BN254;
		}
		case 301:{
			return ProtocolType.SRP_BLS12;
		}
		case 302:{
			return ProtocolType.SRP_BN254;
		}
		default:{
			System.out.println("protocol type:"+id+" is not supported.");
			System.exit(0);
		}
		}
		return null;
	}
}
