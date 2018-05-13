package mx.iteso.distribuidos;

import com.google.gson.Gson;
import mx.iteso.distribuidos.listeners.*;
import mx.iteso.distribuidos.requests.*;
import mx.iteso.distribuidos.response.*;
import mx.iteso.distribuidos.threads.MainThread;
import mx.iteso.distribuidos.threads.NormalExecution;
import mx.iteso.distribuidos.threads.PingThread;
import mx.iteso.distribuidos.threads.VoterThread;
import mx.iteso.distribuidos.utils.ConnectionData;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import static mx.iteso.distribuidos.utils.Constants.*;

public class App {
    private static Map<String, ConnectionData> users = new HashMap<>();
    private static Gson gson = new Gson();
    private static String myIP = getMyIPAddress();
    private static String coordinator = "192.168.1.2";
    private static ArrayList<String> servers = new ArrayList<String>(){{add("192.168.1.2");add("192.168.1.3");/*add("192.168.1.4");*/}};
    private static DatagramSocket clientSocket;
    private static DatagramSocket ipSocket;
    private static CoordinatorListener coordinatorListener;
    private static PingListener pingListener;
    private static VotingListener votingListener;

    public static void main( String[] args ) {
        coordinator = "192.168.1.2";
        //coordinator = "";

        final MainThread mainThread = new MainThread();
        final VoterThread voterThread = new VoterThread();
        final PingThread pingThread = new PingThread();

        VoteTriggerListener voteTriggerListener = new VoteTriggerListener() {
            @Override
            public void onTriggerVote() {
                pingThread.disablePing();
                Voting voting = new Voting();
                String coordinator = voting.vote();
                if (coordinator != null) {
                    mainThread.initialize();
                }
            }
        };

        coordinatorListener = new CoordinatorListener(pingThread);
        pingListener = new PingListener(pingThread, voterThread);
        votingListener = new VotingListener(voteTriggerListener);
        Voting voting = new Voting();
        String coordinator = voting.vote();
        if (coordinator != null)
            mainThread.initialize();

        //pingThread.initialize(voteTriggerListener, myIP);

            /*// TODO PING COORDINATOR
            boolean canPing = true;
            final DatagramSocket sendData = new DatagramSocket();
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            while(canPing) {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            byte[] recieveData = new byte[1024];
                            DatagramPacket receivePacket = new DatagramPacket(recieveData, recieveData.length);
                            sendDatagram(new Ping(), InetAddress.getByName(coordinator), SERVER_PING, sendData);
                            DatagramSocket pingerSocket = new DatagramSocket(SERVER_PING);
                            pingerSocket.receive(receivePacket);
                        } catch (Exception e) {

                        }
                    }
                });
                executorService.shutdown();
                if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
                    canPing = false;
                    executorService.shutdownNow();
                    if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                        System.out.println("Pool do not stops");
                    }
                }
                Thread.sleep(5000);
            }

            // TODO START VOTING


            // TODO start again proccess

            serversSocket = new DatagramSocket(SERVER_PORT);
            clientSocket = new DatagramSocket(CLIENT_PORT);
            ipSocket = new DatagramSocket();
            myIP = getMyIPAddress();
            listener = new Listener();
            listener.start();
            voting();



    }



    private static void changeIP() {
        try {
            for(Map.Entry<String, ConnectionData> user: users.entrySet()) {
                sendDatagram(myIP.getHostAddress(), user.getValue().getIpAddress(), IP_PORT, ipSocket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChangeCoordinator(String newCoordinator) {
        coordinator = newCoordinator;
    }

    private static class Listener extends Thread {
        @Override
        public void run() {
            try {
                byte[] receiveData = new byte[1024];
                while(true)
                {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serversSocket.receive(receivePacket);
                    InetAddress IPAddress = receivePacket.getAddress();
                    int port = receivePacket.getPort();
                    int length = receivePacket.getLength();
                    BaseRequest baseRequest;

                    String request = new String(receivePacket.getData()).substring(0, length);
                    baseRequest = gson.fromJson(request, BaseRequest.class);

                    switch (baseRequest.getType())
                    {
                        case PING:
                            Ping ping = new Ping();
                            sendDatagram(ping, IPAddress, port, serversSocket);
                            break;
                        case VOTE:
                            OkResponse okResponse = new OkResponse();
                            sendDatagram(okResponse, IPAddress, port, serversSocket);
                            voting();
                            break;
                        case COORDINATOR:
                            CoordinatorResponse response = gson.fromJson(request, CoordinatorResponse.class);
                            coordinator = response.getCoordinator();
                            startPings();
                            break;
                        case REMOVE_CLIENT:
                            RemoveClientResponse removeClientResponse = gson.fromJson(request, RemoveClientResponse.class);
                            users.remove(removeClientResponse.getUser());
                            break;
                        case SET_NAME:
                            SetNameResponse setNameResponse = gson.fromJson(request, SetNameResponse.class);
                            users.put(setNameResponse.getUser(), setNameResponse.getConnectionData());
                            break;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private static void voting() {
        ArrayList<String> bullys = getBullies();
        if (listener.isAlive() && !listener.isInterrupted())
            listener.interrupt();

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<Callable<Boolean>> callables = new ArrayList<>();

        for(String bully: bullys) {
            InetAddress address = null;
            try {
                address = InetAddress.getByName(bully);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            final InetAddress realAddress = address;
            callables.add(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    Vote vote = new Vote();
                    String request = gson.toJson(vote, Vote.class);
                    DatagramPacket sendPacket = new DatagramPacket(request.getBytes(), request.length(), realAddress, SERVER_PORT);
                    serversSocket.send(sendPacket);

                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serversSocket.receive(receivePacket);
                    return Boolean.TRUE;
                }
            });
        }

        List<Future<Boolean>> futures = null;
        try {
            futures = executorService.invokeAll(callables);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean responded = false;
        for(Future<Boolean> future : futures) {
            try {
                responded |= future.get();
            } catch (Exception e) {
                responded = false;
            }
        }
        executorService.shutdown();
        if (listener.isInterrupted())
            listener.start();

        if (!responded) {
            changeIP();
            sendCoordinator();
        }
    }





    private static void startPings() {

    }



    private static void sendNewName(SetNameResponse setNameResponse) {
        for(String server: servers) {
            try {
                sendDatagram(setNameResponse, InetAddress.getByName(server), SERVER_PORT, serversSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void sendExitName(String user) {
        RemoveClientResponse removeClientResponse = new RemoveClientResponse(user);
        for(String server: servers) {
            try {
                sendDatagram(removeClientResponse, InetAddress.getByName(server), SERVER_PORT, serversSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/
    }


    private static String getMyIPAddress() {
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
}
