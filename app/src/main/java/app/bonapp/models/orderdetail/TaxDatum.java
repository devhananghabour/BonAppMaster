
package app.bonapp.models.orderdetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaxDatum {

    @SerializedName("order_tax_id")
    @Expose
    private String orderTaxId;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("tax_name")
    @Expose
    private String taxName;
    @SerializedName("tax_percent")
    @Expose
    private String taxPercent;
    @SerializedName("tax_amount")
    @Expose
    private String taxAmount;

    public String getOrderTaxId() {
        return orderTaxId;
    }

    public void setOrderTaxId(String orderTaxId) {
        this.orderTaxId = orderTaxId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public String getTaxPercent() {
        return taxPercent;
    }

    public void setTaxPercent(String taxPercent) {
        this.taxPercent = taxPercent;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

}
