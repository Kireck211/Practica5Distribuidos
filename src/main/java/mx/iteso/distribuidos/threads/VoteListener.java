package mx.iteso.distribuidos.threads;

import com.google.gson.Gson;
import mx.iteso.distribuidos.requests.BaseRequest;
import mx.iteso.distribuidos.requests.Vote;
import mx.iteso.distribuidos.response.CoordinatorResponse;
import mx.iteso.distribuidos.response.OkResponse;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

import static mx.iteso.distribuidos.utils.Constants.*;

public class VoteListener extends Thread {
    private DatagramSocket voteSocket;
    private ArrayList<String> servers;
    private InetAddress myIP;

    public VoteListener(DatagramSocket voteSocket, ArrayList<String> servers, InetAddress IP) {
        this.voteSocket = voteSocket;
        this.servers = servers;
        this.myIP = IP;
    }

    @Override
    public void run() {
        try {
            byte[] receiveData = new byte[1024];
            Gson gson = new Gson();
            while(true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                voteSocket.receive(receivePacket);
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                int length = receivePacket.getLength();

                BaseRequest baseRequest;

                String request = new String(receivePacket.getData()).substring(0, length);
                baseRequest = gson.fromJson(request, BaseRequest.class);

                switch (baseRequest.getType()) {
                    case VOTE:
                        OkResponse okResponse = new OkResponse();
                        sendDatagram(okResponse, IPAddress, SERVER_VOTE, voteSocket);
                        if (!voting()) {
                            sendCoordinator();
                        }
                        break;
                }
            }
        } catch (Exception e) {

        }
    }

    public boolean voting() {
        boolean responded = false;
        try {
            ArrayList<String> bullies = getBullies();
            for(String server: bullies) {
                Vote vote = new Vote();
                sendDatagram(vote, InetAddress.getByName(server), SERVER_VOTE, voteSocket);
            }

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            voteSocket.setSoTimeout(1000);

            while(true) {
                try {
                    voteSocket.receive(receivePacket);
                    responded |= true;
                } catch (SocketTimeoutException e) {
                    System.out.println("No messages");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responded;
    }

    private ArrayList<String> getBullies() {
        ArrayList<String> bullies = new ArrayList<>();
        String address = myIP.getHostAddress();
        for(String bully : servers) {
            if (address.compareTo(bully) < 0)
                bullies.add(bully);
        }
        return bullies;
    }

    private void sendCoordinator() {
        try {
            InetAddress IPAddress;
            CoordinatorResponse coordinatorResponse;
            DatagramSocket coordinatorSocket = new DatagramSocket();
            for(String server: servers) {
                IPAddress = InetAddress.getByName(server);
                coordinatorResponse = new CoordinatorResponse(server);
                sendDatagram(coordinatorResponse, IPAddress, SERVER_COORDINATOR, coordinatorSocket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
