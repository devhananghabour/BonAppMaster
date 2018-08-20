
package app.bonapp.models.countrystatecity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListModel {

    @SerializedName("CODE")
    @Expose
    private Integer cODE;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("countryList")
    @Expose
    private List<DropDownData> countryList = null;
    @SerializedName("stateList")
    @Expose
    private List<DropDownData> stateList = null;
    @SerializedName("cityList")
    @Expose
    private List<DropDownData> cityList = null;

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

    public List<DropDownData> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<DropDownData> countryList) {
        this.countryList = countryList;
    }

    public List<DropDownData> getStateList() {
        return stateList;
    }

    public void setStateList(List<DropDownData> stateList) {
        this.stateList = stateList;
    }

    public List<DropDownData> getCityList() {
        return cityList;
    }

    public void setCityList(List<DropDownData> cityList) {
        this.cityList = cityList;
    }
}
