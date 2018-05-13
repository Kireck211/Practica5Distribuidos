package mx.iteso.distribuidos;

import com.google.gson.Gson;
import mx.iteso.distribuidos.requests.Vote;
import mx.iteso.distribuidos.response.CoordinatorResponse;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.*;

import static mx.iteso.distribuidos.utils.Constants.SERVER_PROMISE;
import static mx.iteso.distribuidos.utils.Constants.SERVER_VOTE;
import static mx.iteso.distribuidos.utils.Constants.sendDatagram;

public class Voting {
    public String vote() {
        ArrayList<String> servers = new ArrayList<String>() {{
            add("192.168.1.2");
            add("192.168.1.3");
            add("192.168.1.4");
        }};
        final Gson gson = new Gson();

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<Callable<Boolean>> callables = new ArrayList<>();
        ArrayList<String> bullies = getBullies(servers);
        for(String server : bullies) {
            InetAddress address = null;
            try {
                address = InetAddress.getByName(server);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            final InetAddress realAddress = address;
            callables.add(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    DatagramSocket socket = new DatagramSocket();
                    Vote vote = new Vote();
                    String request = gson.toJson(vote, Vote.class);
                    DatagramSocket voteSocket = new DatagramSocket(SERVER_PROMISE);
                    byte[] receiveData = new byte[1024];
                    DatagramPacket sendPacket = new DatagramPacket(request.getBytes(), request.length(), realAddress, SERVER_VOTE);
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                    socket.send(sendPacket);
                    voteSocket.receive(receivePacket);
                    return Boolean.TRUE;
                }
            });
        }

        List<Future<Boolean>> futures = null;
        try {
            futures = executorService.invokeAll(callables, 2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean responded = false;
        assert futures != null;
        for(Future<Boolean> future : futures) {
            try {
                responded |= future.get();
            } catch (Exception e) {
                responded = false;
            }
        }
        executorService.shutdown();

        if (!responded) {
            sendCoordinator(servers);
            return getMyIPAddress();
        }
        return null;
    }

    private void sendCoordinator(ArrayList<String> servers) {
        try {
            DatagramSocket sendCoordinatorSocket = new DatagramSocket();
            InetAddress IPAddress;
            CoordinatorResponse coordinatorResponse;
            String myIp = getMyIPAddress();
            for(String server: servers) {
                if (myIp.equals(server))
                    continue;
                IPAddress = InetAddress.getByName(server);
                coordinatorResponse = new CoordinatorResponse(server);
                sendDatagram(coordinatorResponse, IPAddress, SERVER_VOTE, sendCoordinatorSocket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getMyIPAddress() {
        Enumeration e = null;
        try {
            e = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        assert e != null;
        while(e.hasMoreElements())
        {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            if (n.getName().contains("enp0s")) {
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if (i.getHostAddress().contains("192")) {
                        return i.getHostAddress();
                    }
                }
            }
        }
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    private ArrayList<String> getBullies(ArrayList<String> servers) {
        ArrayList<String> bullies = new ArrayList<>();
        String address = getMyIPAddress();
        for(String bully : servers) {
            if (address.compareTo(bully) < 0)
                bullies.add(bully);
        }
        return bullies;
    }
}
