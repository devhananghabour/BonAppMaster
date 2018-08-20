
package app.bonapp.models.signup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import app.bonapp.models.loginmodel.DATA;

public class SignupModel {

    @SerializedName("CODE")
    @Expose
    private Integer cODE;
    @SerializedName("APICODERESULT")
    @Expose
    private String aPICODERESULT;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("DATA")
    @Expose
    private DATA dATA;

    public Integer getCODE() {
        return cODE;
    }

    public void setCODE(Integer cODE) {
        this.cODE = cODE;
    }

    public String getAPICODERESULT() {
        return aPICODERESULT;
    }

    public void setAPICODERESULT(String aPICODERESULT) {
        this.aPICODERESULT = aPICODERESULT;
    }

    public String getMESSAGE() {
        return mESSAGE;
    }

    public void setMESSAGE(String mESSAGE) {
        this.mESSAGE = mESSAGE;
    }

    public DATA getDATA() {
        return dATA;
    }

    public void setDATA(DATA dATA) {
        this.dATA = dATA;
    }

}
