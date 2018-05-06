package mx.iteso.distribuidos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static mx.iteso.distribuidos.utils.Constants.SEND_FILE;

public class ReceiveFileResponse {
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("data")
    @Expose
    private Data data;

    public ReceiveFileResponse() {}

    public ReceiveFileResponse(String user) {
        this.type = SEND_FILE;
        this.data = new Data(user);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        @SerializedName("from")
        @Expose
        private String from;

        public Data() {}

        public Data(String from) {
            this.from = from;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }
    }
}
