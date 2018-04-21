package mx.iteso.distribuidos.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import static mx.iteso.distribuidos.utils.Constants.LIST_USERS;

public class ListUserResponse {
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("resultCode")
    @Expose
    private int resutlCode;

    @SerializedName("data")
    @Expose
    private Data data;

    public class Data {
        @SerializedName("users")
        @Expose
        private List<String> users;

        public Data() {
            users = new ArrayList<>();
        }

        public List<String> getUsers() {
            return users;
        }

        public void setUsers(List<String> users) {
            this.users = users;
        }
    }

    public ListUserResponse() {
        type = LIST_USERS;
        resutlCode = 200;
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

    public int getResutlCode() {
        return resutlCode;
    }

    public void setResutlCode(int resutlCode) {
        this.resutlCode = resutlCode;
    }
}
