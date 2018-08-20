
package app.bonapp.models.orderdetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DealDatum {

    @SerializedName("order_deal_id")
    @Expose
    private String orderDealId;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("deal_id")
    @Expose
    private String dealId;
    @SerializedName("item_name")
    @Expose
    private String itemName;
    @SerializedName("item_quantity")
    @Expose
    private String itemQuantity;
    @SerializedName("item_price")
    @Expose
    private String price="";
    @SerializedName("end_time")
    @Expose
    private String endTime;

    public String getOrderDealId() {
        return orderDealId;
    }

    public void setOrderDealId(String orderDealId) {
        this.orderDealId = orderDealId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
