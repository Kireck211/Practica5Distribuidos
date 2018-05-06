package mx.iteso.distribuidos.utils;

import java.net.InetAddress;

public class ConnectionData {
    private InetAddress ipAddress;
    private int port;
    private boolean sending_File;
    private String file_receiver;

    public ConnectionData() {
    }

    public ConnectionData(InetAddress ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
        sending_File = false;
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
}
