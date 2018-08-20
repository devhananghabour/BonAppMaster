
package app.bonapp.models.merchantposts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Deal {

    @SerializedName("deal_id")
    @Expose
    private String dealId;
    @SerializedName("deal_title")
    @Expose
    private String dealTitle;
    @SerializedName("total_items")
    @Expose
    private String totalItems;
    @SerializedName("original_price")
    @Expose
    private String originalPrice;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("new_price")
    @Expose
    private String newPrice;
    @SerializedName("item_left")
    @Expose
    private String itemLeft;
    @SerializedName("deal_status")
    @Expose
    private String dealStatus;
    @SerializedName("popular")
    @Expose
    private String popular;
    @SerializedName("deal_created_on")
    @Expose
    private String dealCreatedOn;

    @SerializedName("deal_description")
    @Expose
    private String dealDescriptoin;

    private boolean isAddedToCart;
    private String originalQuant;

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getDealTitle() {
        return dealTitle;
    }

    public void setDealTitle(String dealTitle) {
        this.dealTitle = dealTitle;
    }

    public String getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(String totalItems) {
        this.totalItems = totalItems;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(String newPrice) {
        this.newPrice = newPrice;
    }

    public String getItemLeft() {
        return itemLeft;
    }

    public void setItemLeft(String itemLeft) {
        this.itemLeft = itemLeft;
    }

    public String getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(String dealStatus) {
        this.dealStatus = dealStatus;
    }

    public String getPopular() {
        return popular;
    }

    public void setPopular(String popular) {
        this.popular = popular;
    }

    public String getDealCreatedOn() {
        return dealCreatedOn;
    }

    public void setDealCreatedOn(String dealCreatedOn) {
        this.dealCreatedOn = dealCreatedOn;
    }

    public String getDealDescriptoin() {
        return dealDescriptoin;
    }

    public void setDealDescriptoin(String dealDescriptoin) {
        this.dealDescriptoin = dealDescriptoin;
    }

    public boolean isAddedToCart() {
        return isAddedToCart;
    }

    public void setAddedToCart(boolean addedToCart) {
        isAddedToCart = addedToCart;
    }

    public String getOriginalQuant() {
        return originalQuant;
    }

    public void setOriginalQuant(String originalQuant) {
        this.originalQuant = originalQuant;
    }
}
