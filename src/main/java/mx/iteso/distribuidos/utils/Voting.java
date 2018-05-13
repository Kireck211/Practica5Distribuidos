package mx.iteso.distribuidos.utils;

import com.google.gson.Gson;
import mx.iteso.distribuidos.requests.Vote;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.Callable;

public class Voting<Integer> implements Callable<Integer> {
    private InetAddress IPAddress;
    public Voting(InetAddress IPAddress) {
        this.IPAddress = IPAddress;
    }

    @Override
    public Integer call() throws Exception {
        return null;
    }
}
