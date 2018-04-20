package mx.iteso.distribuidos.utils;

import java.net.InetAddress;

public class ConnectionData {
    private InetAddress ipAddress;
    private int port;

    public ConnectionData() {
    }

    public ConnectionData(InetAddress ipAddress, int port) {
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
}
