package mx.iteso.distribuidos.utils;

import com.google.gson.Gson;
import mx.iteso.distribuidos.requests.Vote;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.Callable;
import static mx.iteso.distribuidos.utils.Constants.SERVER_PORT;

public class Voting<Integer> implements Callable<Integer> {
    private InetAddress IPAddress;
    public Voting(InetAddress IPAddress) {
        this.IPAddress = IPAddress;
    }

    @Override
    public Integer call() throws Exception {
        Gson gson = new Gson();
        DatagramSocket socket = new DatagramSocket(SERVER_PORT);
        Vote vote = new Vote();
        String request = gson.toJson(vote, Vote.class);
        DatagramPacket sendPacket = new DatagramPacket(request.getBytes(), request.length(), this.IPAddress, SERVER_PORT);
        socket.send(sendPacket);

        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);
        return null;
    }
}
