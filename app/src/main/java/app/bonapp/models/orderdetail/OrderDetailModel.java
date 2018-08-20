
package app.bonapp.models.orderdetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderDetailModel {

    @SerializedName("CODE")
    @Expose
    private Integer cODE;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("APICODERESULT")
    @Expose
    private String aPICODERESULT;
    @SerializedName("DATA")
    @Expose
    private DATA dATA;

    public Integer getCODE() {
        return cODE;
    }

    public void setCODE(Integer cODE) {
        this.cODE = cODE;
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

    public DATA getDATA() {
        return dATA;
    }

    public void setDATA(DATA dATA) {
        this.dATA = dATA;
    }

}
