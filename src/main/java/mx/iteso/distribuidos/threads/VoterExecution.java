package mx.iteso.distribuidos.threads;

import com.google.gson.Gson;
import mx.iteso.distribuidos.listeners.CoordinatorChangedListener;
import mx.iteso.distribuidos.listeners.PingAgainListener;
import mx.iteso.distribuidos.requests.Vote;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static mx.iteso.distribuidos.utils.Constants.SERVER_VOTE;

public class VoterExecution extends Thread {
    private volatile boolean running = true;
    private CoordinatorChangedListener coordinatorChangedListener;
    private PingAgainListener pingAgainListener;

    public VoterExecution (CoordinatorChangedListener coordinatorChangedListener, PingAgainListener pingAgainListener) {
        this.coordinatorChangedListener = coordinatorChangedListener;
        this.pingAgainListener = pingAgainListener;
    }

    public void terminate() {
        running = false;
    }

    @Override
    public void run() {


    }
}
