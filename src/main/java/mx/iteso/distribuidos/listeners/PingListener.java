package mx.iteso.distribuidos.listeners;

import com.google.gson.Gson;
import mx.iteso.distribuidos.requests.Ping;
import mx.iteso.distribuidos.response.OkResponse;
import mx.iteso.distribuidos.threads.PingThread;
import mx.iteso.distribuidos.threads.VoterThread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import static mx.iteso.distribuidos.utils.Constants.SERVER_PING;
import static mx.iteso.distribuidos.utils.Constants.sendDatagram;

public class PingListener extends Thread {
    private PingThread pingThread;
    private VoterThread voterThread;

    public PingListener(PingThread pingThread, VoterThread voterThread) {
        this.pingThread = pingThread;
        this.voterThread = voterThread;
    }

    @Override
    public void run() {
        try {
            DatagramSocket pingSocket = new DatagramSocket(SERVER_PING);
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            pingSocket.receive(receivePacket);
            InetAddress IPAddress = receivePacket.getAddress();
            sendDatagram(new OkResponse(), IPAddress, SERVER_PING, pingSocket);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
