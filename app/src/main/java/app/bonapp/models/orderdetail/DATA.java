
package app.bonapp.models.orderdetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DATA {

    @SerializedName("orderdata")
    @Expose
    private List<Orderdatum> orderdata = null;
    @SerializedName("deal_data")
    @Expose
    private List<DealDatum> dealData = null;
    @SerializedName("tax_data")
    @Expose
    private List<TaxDatum> taxData = null;

    public List<Orderdatum> getOrderdata() {
        return orderdata;
    }

    public void setOrderdata(List<Orderdatum> orderdata) {
        this.orderdata = orderdata;
    }

    public List<DealDatum> getDealData() {
        return dealData;
    }

    public void setDealData(List<DealDatum> dealData) {
        this.dealData = dealData;
    }

    public List<TaxDatum> getTaxData() {
        return taxData;
    }

    public void setTaxData(List<TaxDatum> taxData) {
        this.taxData = taxData;
    }

}
