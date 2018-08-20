
package app.bonapp.models.merchantposts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MerchantPostsModel {

    @SerializedName("CODE")
    @Expose
    private Integer cODE;
    @SerializedName("next_count")
    @Expose
    private String nextCount;
    @SerializedName("APICODERESULT")
    @Expose
    private String aPICODERESULT;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("DATA")
    @Expose
    private List<DATum> dATA = null;

    public Integer getCODE() {
        return cODE;
    }

    public void setCODE(Integer cODE) {
        this.cODE = cODE;
    }

    public String getNextCount() {
        return nextCount;
    }

    public void setNextCount(String nextCount) {
        this.nextCount = nextCount;
    }

    public String getAPICODERESULT() {
        return aPICODERESULT;
    }

    public void setAPICODERESULT(String aPICODERESULT) {
        this.aPICODERESULT = aPICODERESULT;
    }

    public List<DATum> getDATA() {
        return dATA;
    }

    public void setDATA(List<DATum> dATA) {
        this.dATA = dATA;
    }

    public String getmESSAGE() {
        return mESSAGE;
    }

    public void setmESSAGE(String mESSAGE) {
        this.mESSAGE = mESSAGE;
    }
}
