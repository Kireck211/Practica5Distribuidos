package mx.iteso.distribuidos.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Exit {
    @SerializedName("type")
    @Expose
    private String type;

    public Exit() {}

    public Exit(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
