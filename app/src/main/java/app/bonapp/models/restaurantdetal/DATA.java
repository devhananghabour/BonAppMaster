
package app.bonapp.models.restaurantdetal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import app.bonapp.models.FilterModel;
import app.bonapp.models.merchantposts.Deal;

public class DATA {

    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("sub_description")
    @Expose
    private String subDescription;
    @SerializedName("profile_picture")
    @Expose
    private String profilePicture;
    @SerializedName("total_order_count")
    @Expose
    private String totalOrderCount;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile_country_code")
    @Expose
    private String mobileCountryCode;
    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;
    @SerializedName("active_deals_count")
    @Expose
    private String activeDealsCount;
    @SerializedName("deals")
    private List<Deal> deals = null;
    @SerializedName("min_order")
    private String minOrder;
    @SerializedName("delivery_fees")
    private String deliveryFees;
    @SerializedName("allow_delivery")
    private boolean allowDelivery;
    @SerializedName("delivery_areas")
    private ArrayList<FilterModel> deliveryAreas;
    @SerializedName("delivery_rules")
    private String deliveryRules;


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getTotalOrderCount() {
        return totalOrderCount;
    }

    public void setTotalOrderCount(String totalOrderCount) {
        this.totalOrderCount = totalOrderCount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileCountryCode() {
        return mobileCountryCode;
    }

    public void setMobileCountryCode(String mobileCountryCode) {
        this.mobileCountryCode = mobileCountryCode;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public List<Deal> getDeals() {
        return deals;
    }

    public void setDeals(List<Deal> deals) {
        this.deals = deals;
    }

    public String getActiveDealsCount() {
        return activeDealsCount;
    }

    public void setActiveDealsCount(String activeDealsCount) {
        this.activeDealsCount = activeDealsCount;
    }

    public String getSubDescription() {
        return subDescription;
    }

    public void setSubDescription(String subDescription) {
        this.subDescription = subDescription;
    }

    public String getMinOrder() {
        return minOrder;
    }

    public void setMinOrder(String minOrder) {
        this.minOrder = minOrder;
    }

    public String getDeliveryFees() {
        return deliveryFees;
    }

    public void setDeliveryFees(String deliveryFees) {
        this.deliveryFees = deliveryFees;
    }

    public boolean isAllowDelivery() {
        return allowDelivery;
    }

    public void setAllowDelivery(boolean allowDelivery) {
        this.allowDelivery = allowDelivery;
    }

    public ArrayList<FilterModel> getDeliveryAreas() {
        return deliveryAreas;
    }

    public void setDeliveryAreas(ArrayList<FilterModel> deliveryAreas) {
        this.deliveryAreas = deliveryAreas;
    }

    public String getDeliveryRules() {
        return deliveryRules == null ? "" : deliveryRules;
    }

    public void setDeliveryRules(String deliveryRules) {
        this.deliveryRules = deliveryRules;
    }
}
