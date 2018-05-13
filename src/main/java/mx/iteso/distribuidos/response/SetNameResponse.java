package mx.iteso.distribuidos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import mx.iteso.distribuidos.utils.ConnectionData;

public class SetNameResponse {
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("user")
    @Expose
    private String user;

    @SerializedName("connectionData")
    @Expose
    private ConnectionData connectionData;

    public SetNameResponse() {
        connectionData = new ConnectionData();
    }

    public SetNameResponse(String user, ConnectionData connectionData){
        this.user = user;
        this.connectionData = connectionData;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ConnectionData getConnectionData() {
        return connectionData;
    }

    public void setConnectionData(ConnectionData connectionData) {
        this.connectionData = connectionData;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
