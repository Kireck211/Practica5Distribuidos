package mx.iteso.distribuidos.listeners;

import com.google.gson.Gson;
import mx.iteso.distribuidos.response.CoordinatorResponse;
import mx.iteso.distribuidos.threads.MainThread;
import mx.iteso.distribuidos.threads.PingThread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import static mx.iteso.distribuidos.utils.Constants.SERVER_COORDINATOR;

public class CoordinatorListener extends Thread {
    private PingThread pingThread;
    public CoordinatorListener(PingThread pingThread) {
        this.pingThread = pingThread;
    }

    @Override
    public void run() {
        Gson gson = new Gson();
        try {
            DatagramSocket coordinatorSocket = new DatagramSocket(SERVER_COORDINATOR);
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            coordinatorSocket.receive(receivePacket);
            int length = receivePacket.getLength();
            String request = new String(receivePacket.getData()).substring(0, length);
            CoordinatorResponse coordinatorResponse = gson.fromJson(request, CoordinatorResponse.class);
            pingThread.enablePing(coordinatorResponse.getCoordinator());
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
