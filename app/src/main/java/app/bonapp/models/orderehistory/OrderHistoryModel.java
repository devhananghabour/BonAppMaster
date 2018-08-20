
package app.bonapp.models.orderehistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderHistoryModel {

    @SerializedName("CODE")
    @Expose
    private Integer cODE;
    @SerializedName("next_count")
    @Expose
    private String nextCount;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("APICODERESULT")
    @Expose
    private String aPICODERESULT;
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

    public String getMESSAGE() {
        return mESSAGE;
    }

    public void setMESSAGE(String mESSAGE) {
        this.mESSAGE = mESSAGE;
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

}
