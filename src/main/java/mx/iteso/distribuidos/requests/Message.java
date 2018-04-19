package mx.iteso.distribuidos.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("data")
    @Expose
    private Data data;

    public Message() {}

    public Message(String type, Data data) {
        super();
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
        @SerializedName("to")
        @Expose
        private String to;

        @SerializedName("content")
        @Expose
        private String message;

        public Data() {}

        public Data(String to, String message) {
            this.to = to;
            this.message = message;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}