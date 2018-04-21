package mx.iteso.distribuidos.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseRequest {
    @SerializedName("type")
    @Expose
    private String type;

    public BaseRequest() {}

    public BaseRequest(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "BaseRequest{" +
                "type='" + type + '\'' +
                '}';
    }
}
