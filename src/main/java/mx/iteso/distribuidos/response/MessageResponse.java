package mx.iteso.distribuidos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import static mx.iteso.distribuidos.utils.Constants.MESSAGE_RECEIVED;

public class MessageResponse {
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("resultCode")
    @Expose
    private int resultCode;

    @SerializedName("data")
    @Expose
    private Data data;

    public class Data {
        @SerializedName("from")
        @Expose
        private String from;

        @SerializedName("content")
        @Expose
        private String content;

        public Data() {
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public MessageResponse() {
        type = MESSAGE_RECEIVED;
        resultCode = 200;
        data = new Data();
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
