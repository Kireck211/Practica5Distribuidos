package mx.iteso.distribuidos.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendFile {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("data")
    @Expose
    private Data data;

    public SendFile() { }

    public SendFile(String type, Data data) {
        this.type = type;
        this.data = data;
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
        @SerializedName("receiver")
        @Expose
        private String receiver;

        @SerializedName("name")
        @Expose
        private String name;

        public Data() {}

        public Data(String receiver) {
            this.receiver = receiver;
        }

        public String getReceiver() {
            return receiver;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
