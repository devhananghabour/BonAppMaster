package app.bonapp.models.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class AddressModel implements Parcelable {

    private String id;
    private String name;
    @SerializedName("area_id")
    private String  areaId;
    @SerializedName("area_name")
    private String areaName;
    @SerializedName("city_id")
    private String cityId;
    @SerializedName("city_name")
    private String cityName;
    private String address;
    private String phone;
    private String notes;

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

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.areaId);
        dest.writeString(this.areaName);
        dest.writeString(this.cityId);
        dest.writeString(this.cityName);
        dest.writeString(this.address);
        dest.writeString(this.phone);
        dest.writeString(this.notes);
    }

    public AddressModel() {
    }

    protected AddressModel(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.areaId = in.readString();
        this.areaName = in.readString();
        this.cityId = in.readString();
        this.cityName = in.readString();
        this.address = in.readString();
        this.phone = in.readString();
        this.notes = in.readString();
    }

    public static final Parcelable.Creator<AddressModel> CREATOR = new Parcelable.Creator<AddressModel>() {
        @Override
        public AddressModel createFromParcel(Parcel source) {
            return new AddressModel(source);
        }

        @Override
        public AddressModel[] newArray(int size) {
            return new AddressModel[size];
        }
    };
}
