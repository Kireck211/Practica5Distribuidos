package mx.iteso.distribuidos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static mx.iteso.distribuidos.utils.Constants.REMOVE_CLIENT;

public class RemoveClientResponse {
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("user")
    @Expose
    private String user;

    public RemoveClientResponse() {
        this.type = REMOVE_CLIENT;
    }

    public RemoveClientResponse(String user) {
        this();
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
