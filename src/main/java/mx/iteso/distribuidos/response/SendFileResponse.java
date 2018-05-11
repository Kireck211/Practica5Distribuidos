package mx.iteso.distribuidos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import mx.iteso.distribuidos.requests.SendFile;

import static mx.iteso.distribuidos.utils.Constants.SEND_FILE;
import static mx.iteso.distribuidos.utils.Constants.SET_NAME;

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

    public SendFileResponse(String name) {
        type = SEND_FILE;
        data = new Data(name);
    }

    public class Data {
        @SerializedName("name")
        @Expose
        private String name;

        public Data() {}

        public Data(String name) {
            this.name = name;
        }

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
