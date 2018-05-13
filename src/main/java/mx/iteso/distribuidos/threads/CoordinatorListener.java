package mx.iteso.distribuidos.threads;

import com.google.gson.Gson;
import mx.iteso.distribuidos.ChangeCoordinatorListener;
import mx.iteso.distribuidos.requests.BaseRequest;
import mx.iteso.distribuidos.response.CoordinatorResponse;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import static mx.iteso.distribuidos.utils.Constants.COORDINATOR;

public class CoordinatorListener extends Thread {
    private DatagramSocket coordinatorSocket;
    private ArrayList<ChangeCoordinatorListener> listeners;

    public CoordinatorListener(DatagramSocket coordinatorSocket) {
        this.coordinatorSocket = coordinatorSocket;
        this.listeners = new ArrayList<>();
    }

    public void addChangeCoordinatorListener(ChangeCoordinatorListener listener) {
        this.listeners.add(listener);
    }

    public void removeAllListeners() {
        this.listeners.clear();
    }

    @Override
    public void run() {
        try {
            byte[] receiveData = new byte[1024];
            Gson gson = new Gson();
            while(true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                coordinatorSocket.receive(receivePacket);
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                int length = receivePacket.getLength();

                BaseRequest baseRequest;

                String request = new String(receivePacket.getData()).substring(0, length);
                baseRequest = gson.fromJson(request, BaseRequest.class);

                switch (baseRequest.getType()) {
                    case COORDINATOR:
                        CoordinatorResponse response = gson.fromJson(request, CoordinatorResponse.class);
                        for(ChangeCoordinatorListener listener: listeners) {
                            listener.onChangeCoordinator(response.getCoordinator());
                        }
                        break;
                }
            }
        } catch (Exception e) {

        }
    }
}
