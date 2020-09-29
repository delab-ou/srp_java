
package ou.ist.de.protocol.utils.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Setting {

    @SerializedName("port")
    @Expose
    private String port;
    @SerializedName("frag")
    @Expose
    private String frag;
    @SerializedName("repeat")
    @Expose
    private String repeat;
    @SerializedName("initseq")
    @Expose
    private String initseq;

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getFrag() {
        return frag;
    }

    public void setFrag(String frag) {
        this.frag = frag;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getInitseq() {
        return initseq;
    }

    public void setInitseq(String initseq) {
        this.initseq = initseq;
    }


}
