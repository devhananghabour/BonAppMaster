package app.bonapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by admin1 on 17/10/17.
 */

public class CartItemModel implements Parcelable {

    private String itemName,itemPrice,dealId;
    private int itemQuantity,itemLeft;
    private boolean isEditModeOn;
    private int viewType=1;
    private double percentageTax;
    private String endTime;


    public CartItemModel(Parcel in) {
        itemName = in.readString();
        itemPrice = in.readString();
        dealId = in.readString();
        itemQuantity = in.readInt();
        isEditModeOn = in.readByte() != 0;
        itemLeft=in.readInt();
        viewType=in.readInt();
        percentageTax =in.readDouble();
        endTime=in.readString();
    }

    public static final Creator<CartItemModel> CREATOR = new Creator<CartItemModel>() {
        @Override
        public CartItemModel createFromParcel(Parcel in) {
            return new CartItemModel(in);
        }

        @Override
        public CartItemModel[] newArray(int size) {
            return new CartItemModel[size];
        }
    };

    public CartItemModel() {

    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public boolean isEditModeOn() {
        return isEditModeOn;
    }

    public void setEditModeOn(boolean editModeOn) {
        isEditModeOn = editModeOn;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public int getItemLeft() {
        return itemLeft;
    }

    public void setItemLeft(int itemLeft) {
        this.itemLeft = itemLeft;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public double getPercentageTax() {
        return percentageTax;
    }

    public void setPercentageTax(double percentageTax) {
        this.percentageTax = percentageTax;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(itemName);
        parcel.writeString(itemPrice);
        parcel.writeString(dealId);
        parcel.writeInt(itemQuantity);
        parcel.writeByte((byte) (isEditModeOn ? 1 : 0));
        parcel.writeInt(itemLeft);
        parcel.writeInt(viewType);
        parcel.writeDouble(percentageTax);
        parcel.writeString(endTime);
    }
}
