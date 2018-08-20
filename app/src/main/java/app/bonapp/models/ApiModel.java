package app.bonapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiModel<T> {

    @SerializedName("CODE")
    @Expose
    private int    code;
    @SerializedName("APICODERESULT")
    @Expose
    private String apiCodeResult;
    @SerializedName("MESSAGE")
    @Expose
    private String message;
    @SerializedName("DATA")
    @Expose
    private T      data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getApiCodeResult() {
        return apiCodeResult;
    }

    public void setApiCodeResult(String apiCodeResult) {
        this.apiCodeResult = apiCodeResult;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
