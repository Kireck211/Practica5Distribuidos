package mx.iteso.distribuidos.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BlockUser {
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("data")
    @Expose
    private Data data;

    public BlockUser(){}

    public BlockUser(String type, Data data) {
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
        @SerializedName("user")
        @Expose
        private String user;

        public Data() {}

        public Data(String user) {
            this.user = user;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }
    }

}
