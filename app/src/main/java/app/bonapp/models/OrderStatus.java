package app.bonapp.models;

import android.os.Parcel;

import app.bonapp.fragments.ListDialogFragment;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class OrderStatus implements ListDialogFragment.DialogList{

    public enum OrderStatuses {
        ORDER_PLACED("0"),
        ORDER_ACCEPTED("1"),
        ORDER_ON_THE_WAY("2"),
        ORDER_DELIVERED("3");

        private String value;

        OrderStatuses(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private int id;
    private String title;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Object getObject() {
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
    }

    public OrderStatus() {
    }

    protected OrderStatus(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
    }

    public static final Creator<OrderStatus> CREATOR = new Creator<OrderStatus>() {
        @Override
        public OrderStatus createFromParcel(Parcel source) {
            return new OrderStatus(source);
        }

        @Override
        public OrderStatus[] newArray(int size) {
            return new OrderStatus[size];
        }
    };
}
