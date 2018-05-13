package mx.iteso.distribuidos.listeners;

import com.google.gson.Gson;
import mx.iteso.distribuidos.requests.BaseRequest;
import mx.iteso.distribuidos.requests.Vote;
import mx.iteso.distribuidos.response.OkResponse;
import mx.iteso.distribuidos.threads.MainThread;
import mx.iteso.distribuidos.threads.PingThread;
import mx.iteso.distribuidos.threads.VoterThread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import static mx.iteso.distribuidos.utils.Constants.SERVER_PROMISE;
import static mx.iteso.distribuidos.utils.Constants.SERVER_VOTE;
import static mx.iteso.distribuidos.utils.Constants.sendDatagram;

public class VotingListener extends Thread {
    private VoteTriggerListener voteTriggerListener;

    public VotingListener(VoteTriggerListener voteTriggerListener) {
        this.voteTriggerListener = voteTriggerListener;
    }

    @Override
    public void run() {
        try {
            Gson gson = new Gson();
            DatagramSocket votingSocket = new DatagramSocket(SERVER_VOTE);
            DatagramSocket sendOk = new DatagramSocket();
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            votingSocket.receive(receivePacket);
            InetAddress IPAddress = receivePacket.getAddress();
            sendDatagram(new OkResponse(), IPAddress, SERVER_PROMISE, sendOk);
            voteTriggerListener.onTriggerVote();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
