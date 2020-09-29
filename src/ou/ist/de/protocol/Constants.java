package ou.ist.de.protocol;

public class Constants {
	public static int InetAddressLength = 4;
	public static int PORT = 30000;
	public static int RCVBUFFER = 2000;
	public static String BROAD_CAST_ADDR = "10.255.255.255";
	public static byte REQ = 0;
	public static byte REP = 1;
	public static byte ERR = 2;
	public static byte DATA = 3;
	public static long timer;
	public static boolean wait;
	public static int FSIZE = 1000;
	public static int REPEAT=1;
	public static int INIT_SEQ=1;
	public static int[] network = new int[] { 10, 0, 0 };
	public static long TIMEOUT = 10000000;
	public static int SignatureBitLength=1024;
	
	public static int ARAN_EXPIRED_TIME = 100000;

	public static String JPBC_A="a";
	public static String JPBC_A1="a1";
	public static String JPBC_e="e";
	public static String MCL_BLS12_381="bls12_381";
	public static String MCL_BN254="bn254";

	public static String DEFAULT_ISDSR_JPBC_KEY="jpbc_a.keys";
	public static String DEFAULT_ISDSR_JPBC_PARAM="a";
	public static String DEFAULT_ISDSR_MCL_KEYS="mcl_bn254.keys";
	public static String DEFAULT_RSA_SIG_BIT_LENGTH="1024";
	public static String DEFAULT_RSA_KEY_INDEX="10";
	public static String DEFAULT_INTERVAL_MILISEC="1000";
	public static String DEFAULT_REPEAT_TIMES="1";
	public static String DEFAULT_INITIAL_SEQUENCE_NUM="1";
	public static String DEFAULT_PORT_NUM="30000";
	public static String DEFAULT_FRAGMENTATION_SIZE="1000";
	
	public static String ARG_DEFAULT_SETTING="-default-setting";
	public static String ARG_CONFIG_PROTO_SETTING="-config-protoid";
	public static String ARG_SIG_ALGO="-sig-algo";
	public static String ARG_SIG_TYPE="-sig-type";
	public static String ARG_SIG_KEY_FILE="-sig-keyfile";
	public static String ARG_SIG_BIT_LENGTH = "-sigbitlength";
	public static String ARG_KEY_INDEX="-keyindex";
	public static String ARG_PORT_NUM = "-port";
	public static String ARG_PROTO="-protocol";
	public static String ARG_FRAGMENTATION_SIZE = "-frag";
	public static String ARG_DESTINATION = "-dest";
	public static String ARG_PROTO_AODV="AODV";
	public static String ARG_PROTO_DSR="DSR";
	public static String ARG_PROTO_ISDSR="ISDSR";
	public static String ARG_PROTO_SRP="SRP";
	public static String ARG_PROTO_ISDSR_JPBC="ISDSR_JPBC";
	public static String ARG_PROTO_ISDSR_MCL="ISDSR_MCL";
	public static String ARG_PROTO_RSA="RSA";
	public static String ARG_PROTO_SRDP="SRDP";
	public static String ARG_PROTO_SRDP_GDH="SRDP_GDH";
	public static String ARG_REPEAT="-repeat";
	public static String ARG_INTERVAL="-interval";
	public static String ARG_INITIAL_SEQUENCE_NUM="-seq";
	public static String ARG_MEASURE_REGULAR_RTT="-measure-reg-rtt";
	
}
