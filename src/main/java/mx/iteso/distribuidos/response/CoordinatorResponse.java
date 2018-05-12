package mx.iteso.distribuidos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static mx.iteso.distribuidos.utils.Constants.COORDINATOR;

public class CoordinatorResponse {
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("coordinator")
    @Expose
    private String coordinator;

    public CoordinatorResponse() {
        this.type = COORDINATOR;
    }

    public CoordinatorResponse(String coordinator) {
        this();
        this.coordinator = coordinator;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(String coordinator) {
        this.coordinator = coordinator;
    }
}
