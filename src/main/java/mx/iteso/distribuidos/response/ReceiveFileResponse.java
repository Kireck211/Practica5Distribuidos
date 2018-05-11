package mx.iteso.distribuidos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static mx.iteso.distribuidos.utils.Constants.RECEIVE_FILE;

public class ReceiveFileResponse {
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("data")
    @Expose
    private Data data;

    public ReceiveFileResponse() {}

    public ReceiveFileResponse(String user, String name) {
        this.type = RECEIVE_FILE;
        this.data = new Data(user, name);
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

        @SerializedName("name")
        @Expose
        private String name;

        public Data() {}

        public Data(String from, String name) {
            this.from = from;
            this.name = name;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
