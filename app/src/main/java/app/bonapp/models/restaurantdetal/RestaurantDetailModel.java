
package app.bonapp.models.restaurantdetal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestaurantDetailModel {

    @SerializedName("CODE")
    @Expose
    private Integer cODE;
    @SerializedName("APICODERESULT")
    @Expose
    private String aPICODERESULT;
    @SerializedName("DATA")
    @Expose
    private DATA dATA;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;

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

    public DATA getDATA() {
        return dATA;
    }

    public void setDATA(DATA dATA) {
        this.dATA = dATA;
    }

    public String getmESSAGE() {
        return mESSAGE;
    }

    public void setmESSAGE(String mESSAGE) {
        this.mESSAGE = mESSAGE;
    }
}
