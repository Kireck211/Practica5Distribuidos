package mx.iteso.distribuidos.threads;

import com.google.gson.Gson;
import mx.iteso.distribuidos.ChangeCoordinatorListener;
import mx.iteso.distribuidos.requests.BaseRequest;
import mx.iteso.distribuidos.requests.Ping;
import mx.iteso.distribuidos.response.OkResponse;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import static mx.iteso.distribuidos.utils.Constants.*;

public class PingListener extends Thread implements ChangeCoordinatorListener {
    private DatagramSocket votingSocket;
    private DatagramSocket pingSocket;
    private String coordinator;

    public PingListener (DatagramSocket pingSocket, String coordinator, DatagramSocket votingSocket) {
        this.pingSocket = pingSocket;
        this.coordinator = coordinator;
        this.votingSocket = votingSocket;
    }

    @Override
    public void run() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        try {
                            System.out.println("Enviando ping al coordinador " + coordinator);
                            pingSocket.setSoTimeout(4000);
                            Ping ping = new Ping();
                            InetAddress address = InetAddress.getByName(coordinator);
                            sendDatagram(ping, address, SERVER_PING, pingSocket);
                        } catch (SocketTimeoutException e) {
                            System.out.println("Ping not responded");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                },
                0,
                5000
        );

        try {
            byte[] receiveData = new byte[1024];
            Gson gson = new Gson();
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                pingSocket.receive(receivePacket);
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                int length = receivePacket.getLength();

                BaseRequest baseRequest;

                String request = new String(receivePacket.getData()).substring(0, length);
                baseRequest = gson.fromJson(request, BaseRequest.class);

                switch (baseRequest.getType()) {
                    case PING:
                        OkResponse okResponse = new OkResponse();
                        sendDatagram(okResponse, IPAddress, SERVER_PING, pingSocket);
                        break;
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onChangeCoordinator(String newCoordinator) {
        this.coordinator = newCoordinator;
    }
}
