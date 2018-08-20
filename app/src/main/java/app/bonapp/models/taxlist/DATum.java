
package app.bonapp.models.taxlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DATum {

    @SerializedName("tax_id")
    @Expose
    private String taxId;
    @SerializedName("tax_name")
    @Expose
    private String taxName;
    @SerializedName("tax_percentage")
    @Expose
    private String taxPercentage;
    @SerializedName("tax_status")
    @Expose
    private String taxStatus;
    @SerializedName("tax_added_on")
    @Expose
    private String taxAddedOn;

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public String getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(String taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public String getTaxStatus() {
        return taxStatus;
    }

    public void setTaxStatus(String taxStatus) {
        this.taxStatus = taxStatus;
    }

    public String getTaxAddedOn() {
        return taxAddedOn;
    }

    public void setTaxAddedOn(String taxAddedOn) {
        this.taxAddedOn = taxAddedOn;
    }

}
