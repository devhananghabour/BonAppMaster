
package app.bonapp.models.merchantstats;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DATA {

    @SerializedName("Result")
    @Expose
    private Result result;
    @SerializedName("type")
    @Expose
    private String type;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
