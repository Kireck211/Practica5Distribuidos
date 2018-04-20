package mx.iteso.distribuidos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static mx.iteso.distribuidos.utils.Constants.RESPONSE;
import static mx.iteso.distribuidos.utils.Constants.RESPONSE_ERROR_CODE;

public class ErrorResponse {
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("resultCode")
    @Expose
    private int resultCode;

    @SerializedName("error")
    @Expose
    private String error;

    public ErrorResponse() {}

    public ErrorResponse(String error) {
        this.type = RESPONSE;
        this.resultCode = RESPONSE_ERROR_CODE;
        this.error = error;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
