
package app.bonapp.models.merchantdeals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DATum implements Parcelable {

    @SerializedName("deal_id")
    @Expose
    private String dealId;
    @SerializedName("total_items")
    @Expose
    private String totalItems;
    @SerializedName("deal_title")
    @Expose
    private String dealTitle;
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
    @SerializedName("deal_created_on")
    @Expose
    private String dealCreatedOn;
    @SerializedName("item_sold")
    @Expose
    private String itemSold;
    @SerializedName("percentage")
    @Expose
    private String percentage;
    @SerializedName("deal_description")
    @Expose
    private String dealDescription;


    protected DATum(Parcel in) {
        dealId = in.readString();
        totalItems = in.readString();
        dealTitle = in.readString();
        originalPrice = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        newPrice = in.readString();
        itemLeft = in.readString();
        dealStatus = in.readString();
        dealCreatedOn = in.readString();
        itemSold = in.readString();
        percentage = in.readString();
        dealDescription = in.readString();
    }

    public static final Creator<DATum> CREATOR = new Creator<DATum>() {
        @Override
        public DATum createFromParcel(Parcel in) {
            return new DATum(in);
        }

        @Override
        public DATum[] newArray(int size) {
            return new DATum[size];
        }
    };

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(String totalItems) {
        this.totalItems = totalItems;
    }

    public String getDealTitle() {
        return dealTitle;
    }

    public void setDealTitle(String dealTitle) {
        this.dealTitle = dealTitle;
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

    public String getDealCreatedOn() {
        return dealCreatedOn;
    }

    public void setDealCreatedOn(String dealCreatedOn) {
        this.dealCreatedOn = dealCreatedOn;
    }

    public String getItemSold() {
        return itemSold;
    }

    public void setItemSold(String itemSold) {
        this.itemSold = itemSold;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getDealDescription() {
        return dealDescription;
    }

    public void setDealDescription(String dealDescription) {
        this.dealDescription = dealDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dealId);
        dest.writeString(totalItems);
        dest.writeString(dealTitle);
        dest.writeString(originalPrice);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(newPrice);
        dest.writeString(itemLeft);
        dest.writeString(dealStatus);
        dest.writeString(dealCreatedOn);
        dest.writeString(itemSold);
        dest.writeString(percentage);
        dest.writeString(dealDescription);
    }
}
