
package ou.ist.de.protocol.utils.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class ConfigFile {

    @SerializedName("setting")
    @Expose
    private Setting setting;
    @SerializedName("sig")
    @Expose
    private List<Sig> sig = null;
    @SerializedName("protocolsetting")
    @Expose
    private List<Protocolsetting> protocolsetting = null;
    
    public ConfigFile readFile(String file) {
    	String json="";
    	try {
    		BufferedReader bb=new BufferedReader(new FileReader(file));
    		String tmp=null;
    		while((tmp=bb.readLine())!=null) {
    			json+=tmp;
    		}
    		bb.close();
    		Gson gson=new Gson();
    		ConfigFile model=gson.fromJson(json, ConfigFile.class);
    		return model;
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    
    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    public List<Sig> getSig() {
        return sig;
    }

    public void setSig(List<Sig> sig) {
        this.sig = sig;
    }

    public List<Protocolsetting> getProtocolsetting() {
        return protocolsetting;
    }

    public void setProtocolsetting(List<Protocolsetting> protocolsetting) {
        this.protocolsetting = protocolsetting;
    }

}
