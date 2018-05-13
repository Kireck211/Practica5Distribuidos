package mx.iteso.distribuidos.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.InetAddress;
import java.util.ArrayList;

public class ConnectionData {
    @SerializedName("ipAddress")
    @Expose
    private InetAddress ipAddress;
    @SerializedName("port")
    @Expose
    private int port;
    @SerializedName("sending_File")
    @Expose
    private boolean sending_File;
    @SerializedName("file_receiver")
    @Expose
    private String file_receiver;
    @SerializedName("blockedUsers")
    @Expose
    private ArrayList<String> blockedUsers;

    public ConnectionData() {
        sending_File = false;
        file_receiver = "";
        blockedUsers = new ArrayList<>();
    }

    public ConnectionData(InetAddress ipAddress, int port) {
        this();
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean equals(ConnectionData obj) {
        return this.ipAddress.equals(obj.ipAddress) && this.port == obj.port;
    }

    public boolean isSending_File() {
        return sending_File;
    }

    public void setSending_File(boolean sending_File) {
        this.sending_File = sending_File;
    }

    public String getFile_receiver() {
        return file_receiver;
    }

    public void setFile_receiver(String file_receiver) {
        this.file_receiver = file_receiver;
    }

    public ArrayList<String> getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(ArrayList<String> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }
}
