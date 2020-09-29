package ou.ist.de.protocol.utils;

import java.util.HashMap;

import ou.ist.de.protocol.Constants;
import ou.ist.de.protocol.utils.config.ConfigFile;
import ou.ist.de.protocol.utils.config.Protocolsetting;
import ou.ist.de.protocol.utils.config.Setting;
import ou.ist.de.protocol.utils.config.Sig;

public class RuntimeArgs {
	protected ConfigFile cf;
	
	public RuntimeArgs() {
		cf = new ConfigFile().readFile("config.json");
	}
	public void setArgs(String[] args, HashMap<String,String> params) {
		params.clear();
		for (int i = 0; i < args.length; i++) {
			String[] t = args[i].split(":");
			params.put(t[0], t[1]);
		}
		if (!params.containsKey(Constants.ARG_INTERVAL)) {
			params.put(Constants.ARG_INTERVAL, "1000");
		}
		if (params.containsKey(Constants.ARG_DEFAULT_SETTING)) {
			if (params.get(Constants.ARG_DEFAULT_SETTING).equalsIgnoreCase("on")) {
				Setting s = cf.getSetting();
				params.put(Constants.ARG_PORT_NUM, s.getPort());
				params.put(Constants.ARG_INITIAL_SEQUENCE_NUM, s.getInitseq());
				params.put(Constants.ARG_FRAGMENTATION_SIZE, s.getFrag());
				params.put(Constants.ARG_REPEAT, s.getRepeat());
			} else {
				System.out.println("default setting is not set \"on\"");
				System.exit(0);
			}
		} else {
			this.checkParameters(params, Constants.ARG_PORT_NUM, Constants.DEFAULT_PORT_NUM);
			this.checkParameters(params, Constants.ARG_INITIAL_SEQUENCE_NUM,
					Constants.DEFAULT_INITIAL_SEQUENCE_NUM);
			this.checkParameters(params, Constants.ARG_REPEAT, Constants.DEFAULT_REPEAT_TIMES);
			this.checkParameters(params, Constants.ARG_FRAGMENTATION_SIZE, Constants.DEFAULT_FRAGMENTATION_SIZE);
		}

		Constants.PORT = Integer.valueOf(params.get(Constants.ARG_PORT_NUM));
		Constants.INIT_SEQ = Integer.valueOf(params.get(Constants.ARG_INITIAL_SEQUENCE_NUM));
		Constants.REPEAT = Integer.valueOf(params.get(Constants.ARG_REPEAT));
		Constants.FSIZE = Integer.valueOf(params.get(Constants.ARG_FRAGMENTATION_SIZE));

		Protocolsetting ps = null;
		Sig sig = null;
		if (params.containsKey(Constants.ARG_CONFIG_PROTO_SETTING)) {
			String id = params.get(Constants.ARG_CONFIG_PROTO_SETTING);
			for (Protocolsetting p : cf.getProtocolsetting()) {
				if (p.getId().equalsIgnoreCase(id)) {
					ps = p;
					break;
				}
			}
			if (ps == null) {
				System.out.println("ID number of the protocolsettgin is not supported");
				System.exit(0);
			}
			for (Sig s : cf.getSig()) {
				if (s.getId().equalsIgnoreCase(ps.getSig())) {
					sig = s;
					break;
				}
			}
			if (sig == null) {
				System.out.println("sig id in the protocolsetting " + ps.getId() + " is not supported");
				System.exit(0);
			}
			params.put(Constants.ARG_PROTO, ps.getName());
			params.put(Constants.ARG_SIG_ALGO, sig.getName());
			params.put(Constants.ARG_SIG_TYPE, sig.getType());
			params.put(Constants.ARG_SIG_KEY_FILE, sig.getKeys());
		} else {
			if (!params.containsKey(Constants.ARG_PROTO)) {
				System.out.println("The target protocol is not defined");
				System.exit(0);
			}
			if (!params.containsKey(Constants.ARG_SIG_ALGO)) {
				System.out.println("The target signature algorithm is not defined");
				System.exit(0);
			}

			if (!params.containsKey(Constants.ARG_SIG_TYPE)) {
				System.out.println("The target signature algorithm is not defined");
				System.exit(0);
			}
			for (Protocolsetting p : cf.getProtocolsetting()) {
				if (p.getName().equalsIgnoreCase(params.get(Constants.ARG_PROTO))) {
					for (Sig s : cf.getSig()) {
						if (s.getId().equalsIgnoreCase(p.getSig())) {
							if (s.getName().equalsIgnoreCase(params.get(Constants.ARG_SIG_ALGO))) {
								ps = p;
								sig = s;
								params.put(Constants.ARG_SIG_KEY_FILE, sig.getKeys());
								break;
							}
						}
					}
				}
				if (ps != null) {
					break;
				}
			}
			if (ps == null) {
				System.out.println("The protocol " + params.get(Constants.ARG_PROTO) + " is not supported");
				System.exit(0);
			}
			if (sig == null) {
				System.out.println(
						"The signature algorithm " + params.get(Constants.ARG_SIG_ALGO) + " is not supported");
				System.exit(0);

			}
		}
	}

	public String checkParameters(HashMap<String, String> params, String key, String value) {
		if (!params.containsKey(key)) {
			params.put(key, value);
			return value;
		} else {
			return params.get(key);
		}
	}

	public boolean checkDestination(HashMap<String, String> args) {
		if (!args.containsKey(Constants.ARG_DESTINATION)) {
			System.out.println("The destination is not defined");
			return false;
		}
		return true;
	}
}
