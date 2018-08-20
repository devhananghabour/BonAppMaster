package app.bonapp.models;

public enum DeliveryType {

    PICKUP_DINE_IN("1"),
    DELIVERY("3");

    DeliveryType(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
