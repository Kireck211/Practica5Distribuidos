package mx.iteso.distribuidos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OkResponse {
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("resultCode")
    @Expose
    private int resultCode;

    public OkResponse() {
        type = "response";
        resultCode = 200;
    }
}
