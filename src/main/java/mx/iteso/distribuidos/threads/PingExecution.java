package mx.iteso.distribuidos.threads;

import mx.iteso.distribuidos.listeners.VoteTriggerListener;
import mx.iteso.distribuidos.response.OkResponse;

import java.io.IOException;
import java.net.*;

import static mx.iteso.distribuidos.utils.Constants.SERVER_PING;
import static mx.iteso.distribuidos.utils.Constants.sendDatagram;

public class PingExecution extends Thread {
    private volatile boolean running = true;
    private volatile boolean sendPing = true;
    private VoteTriggerListener voteTriggerListener;
    private String coordinator = "192.168.1.2";
    private String myIP;

    public PingExecution(VoteTriggerListener voteTriggerListener, String myIP) {
        this.voteTriggerListener = voteTriggerListener;
        this.myIP = myIP;
    }

    public void enablePing(String coordinator) {
        sendPing = true;
        this.coordinator = coordinator;
    }

    public void disablePing() {
        sendPing = false;
    }

    public void terminate() {
        running = false;
        sendPing = false;
    }

    @Override
    public void run() {
            try {
                DatagramSocket pingSocket = new DatagramSocket(SERVER_PING);
                DatagramSocket sendSocket = new DatagramSocket();
                pingSocket.setSoTimeout(1000);
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                while (running) {
                    if (sendPing) {
                        if (myIP.equals(coordinator))
                            continue;
                        sendDatagram(new OkResponse(), InetAddress.getByName(coordinator), SERVER_PING, sendSocket);
                        pingSocket.receive(receivePacket);
                        System.out.println("Ping received");
                    }
                    Thread.sleep(5000);
                }
            } catch (SocketTimeoutException e) {
                voteTriggerListener.onTriggerVote();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}
