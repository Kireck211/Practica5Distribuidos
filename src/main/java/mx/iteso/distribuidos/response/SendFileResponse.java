package mx.iteso.distribuidos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static mx.iteso.distribuidos.utils.Constants.SEND_FILE;

public class SendFileResponse {
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("data")
    @Expose
    private Data data;

    public SendFileResponse() {
        type = SEND_FILE;
        data = new Data();
    }

    public class Data {
        @SerializedName("name")
        @Expose
        private String name;

        public Data() {}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
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
}
