package mx.iteso.distribuidos.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static mx.iteso.distribuidos.utils.Constants.PING;

public class Ping {
    @SerializedName("type")
    @Expose
    private String type;

    public Ping(){
        type = PING;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
