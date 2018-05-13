package mx.iteso.distribuidos.listeners;

import com.google.gson.Gson;
import mx.iteso.distribuidos.threads.MainThread;
import mx.iteso.distribuidos.threads.PingThread;
import mx.iteso.distribuidos.threads.VoterThread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import static mx.iteso.distribuidos.utils.Constants.SERVER_VOTE;

public class VotingListener extends Thread {
    private VoteTriggerListener voteTriggerListener;

    public VotingListener(VoteTriggerListener voteTriggerListener) {
        this.voteTriggerListener = voteTriggerListener;
    }

    @Override
    public void run() {
        try {
            DatagramSocket votingSocket = new DatagramSocket(SERVER_VOTE);
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            votingSocket.receive(receivePacket);
            int length = receivePacket.getLength();
            String request = new String(receivePacket.getData()).substring(0, length);
            voteTriggerListener.onTriggerVote();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
