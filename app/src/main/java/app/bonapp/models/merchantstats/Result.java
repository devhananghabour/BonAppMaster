
package app.bonapp.models.merchantstats;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("total_item_for_sale")
    @Expose
    private String totalItemForSale;
    @SerializedName("item_sold")
    @Expose
    private String itemSold;
    @SerializedName("sold_percentage")
    @Expose
    private String soldPercentage;
    @SerializedName("deal_created")
    @Expose
    private String dealCreated;
    @SerializedName("sold_for")
    @Expose
    private String soldFor;
    @SerializedName("sold_for_wtx")
    @Expose
    private String soldForWtx;
    @SerializedName("your_income_percent")
    @Expose
    private String yourIncomePercent;
    @SerializedName("your_income")
    @Expose
    private String yourIncome;
    @SerializedName("your_income_wtx")
    @Expose
    private String yourIncomeWtx;

    public String getTotalItemForSale() {
        return totalItemForSale;
    }

    public void setTotalItemForSale(String totalItemForSale) {
        this.totalItemForSale = totalItemForSale;
    }

    public String getItemSold() {
        return itemSold;
    }

    public void setItemSold(String itemSold) {
        this.itemSold = itemSold;
    }

    public String getSoldPercentage() {
        return soldPercentage;
    }

    public void setSoldPercentage(String soldPercentage) {
        this.soldPercentage = soldPercentage;
    }

    public String getDealCreated() {
        return dealCreated;
    }

    public void setDealCreated(String dealCreated) {
        this.dealCreated = dealCreated;
    }

    public String getSoldFor() {
        return soldFor;
    }

    public void setSoldFor(String soldFor) {
        this.soldFor = soldFor;
    }

    public String getSoldForWtx() {
        return soldForWtx;
    }

    public void setSoldForWtx(String soldForWtx) {
        this.soldForWtx = soldForWtx;
    }

    public String getYourIncomePercent() {
        return yourIncomePercent;
    }

    public void setYourIncomePercent(String yourIncomePercent) {
        this.yourIncomePercent = yourIncomePercent;
    }

    public String getYourIncome() {
        return yourIncome;
    }

    public void setYourIncome(String yourIncome) {
        this.yourIncome = yourIncome;
    }

    public String getYourIncomeWtx() {
        return yourIncomeWtx;
    }

    public void setYourIncomeWtx(String yourIncomeWtx) {
        this.yourIncomeWtx = yourIncomeWtx;
    }

}
