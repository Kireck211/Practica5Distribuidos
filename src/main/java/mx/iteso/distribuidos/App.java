package mx.iteso.distribuidos;

import com.google.gson.Gson;
import mx.iteso.distribuidos.requests.*;
import mx.iteso.distribuidos.response.*;
import mx.iteso.distribuidos.utils.ConnectionData;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import static mx.iteso.distribuidos.utils.Constants.*;

public class App
{
    private static Map<String, ConnectionData> users;
    private static Gson gson = new Gson();
    private static InetAddress myIP;
    private static String coordinator = "192.168.1.2";
    private static ArrayList<String> servers = new ArrayList<String>(){{add("192.168.1.2");add("192.168.1.3");add("192.168.1.4");}};
    private static DatagramSocket serversSocket;

    public static void main( String[] args ) {

        myIP = getMyIPAddress();
        Listener listener = new Listener();
        listener.start();
        voting();

        try {
            serversSocket = new DatagramSocket(SERVER_PORT);
            DatagramSocket serverSocket = new DatagramSocket(PORT);
            byte[] receiveData = new byte[1024];
            users = new HashMap<>();
            while(true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                int length = receivePacket.getLength();

                String from = getUser(IPAddress, port);
                BaseRequest baseRequest;
                if (from != null && users.get(from).isSending_File()) {
                    ConnectionData receiver = users.get(users.get(from).getFile_receiver());
                    try {
                        String request = new String(receivePacket.getData()).substring(0, length);
                        baseRequest = gson.fromJson(request, BaseRequest.class);
                        users.get(from).setSending_File(false);
                        users.get(from).setFile_receiver("");
                    } catch (Exception e) {
                        sendFilePackage(receiver.getIpAddress(), receiver.getPort(), serverSocket, receivePacket);
                        continue;
                    }
                }
                String request = new String(receivePacket.getData()).substring(0, length);
                baseRequest = gson.fromJson(request, BaseRequest.class);
                if (from == null && !baseRequest.getType().equals(SET_NAME)) {
                    ErrorResponse errorResponse = new ErrorResponse(NOT_REGISTERED);
                    String response = gson.toJson(errorResponse, ErrorResponse.class);
                    DatagramPacket sendPacket = new DatagramPacket(response.getBytes(),
                            response.length(),
                            IPAddress,
                            port);
                    serverSocket.send(sendPacket);
                    continue;
                }

                System.out.println(request + PORT);

                switch (baseRequest.getType()) {
                    case SET_NAME:
                        setName(request, IPAddress, port, serverSocket);
                        break;
                    case SEND_MESSAGE:
                        sendMessage(request, IPAddress, port, serverSocket);
                        break;
                    case LIST_USERS:
                        listUsers(IPAddress, port, serverSocket);
                        break;
                    case SEND_FILE:
                        registerFileRequest(from, IPAddress, port, serverSocket, request);
                        break;
                    case FILE_SENT:
                        fileSent(from);
                        break;
                    case BLOCK_USER:
                        blockUser(request, IPAddress, port, serverSocket);
                        break;
                    case EXIT:
                        exit(IPAddress, port);
                        break;
                    default:
                        notValidRequestError(IPAddress, port, serverSocket);
                }

            }
        } catch (SocketException e) {
            System.out.println("Socket Exception");
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException Exception");
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (Exception e)  {
            System.out.println("Other Exception");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }


    }

    private static String getUser(InetAddress IPAddress, int port) {
        ConnectionData search = new ConnectionData(IPAddress, port);
        for(Map.Entry<String, ConnectionData> user: users.entrySet()) {
            if (user.getValue().equals(search)){
                return user.getKey();
            }
        }
        return null;
    }

    private static void setName(String request, InetAddress IPAddress, int port, DatagramSocket serverSocket) throws IOException {
        SetUser setUser = gson.fromJson(request, SetUser.class);
        if (!users.containsKey(setUser.getData().getContent())) {
            users.put(setUser.getData().getContent(), new ConnectionData(IPAddress, port));
            OkResponse ok = new OkResponse(SET_NAME);
            String response = gson.toJson(ok, OkResponse.class);
            DatagramPacket sendPacket = new DatagramPacket(response.getBytes(),
                    response.length(),
                    IPAddress,
                    port);
            serverSocket.send(sendPacket);
        } else {
            ErrorResponse error = new ErrorResponse(USER_TAKEN);
            String response = gson.toJson(error, ErrorResponse.class);
            DatagramPacket sendPacket = new DatagramPacket(response.getBytes(),
                    response.length(),
                    IPAddress,
                    port);
            serverSocket.send(sendPacket);
        }
    }

    private static void sendMessage(String request, InetAddress IPAddress, int port, DatagramSocket serverSocket) throws IOException {
        Message message = gson.fromJson(request, Message.class);
        String from = getUser(IPAddress, port);
        String to = message.getData().getTo();

        if(users.get(from).getBlockedUsers().contains(to))
            return;

        if (from == null)
            return;
        if (message.getData().getTo().equals("all")) {
            DatagramPacket sendPacket;
            for (Map.Entry<String, ConnectionData> entry : users.entrySet()) {
                if (entry.getKey().equals(from))
                    continue;
                MessageResponse messageResponse = new MessageResponse();
                messageResponse.getData().setFrom(from);
                messageResponse.getData().setContent(message.getData().getMessage());
                String response = gson.toJson(messageResponse, MessageResponse.class);
                sendPacket = new DatagramPacket(response.getBytes(),
                        response.length(),
                        entry.getValue().getIpAddress(),
                        entry.getValue().getPort());
                serverSocket.send(sendPacket);
            }
            return;
        }
        ConnectionData connectionData = users.get(message.getData().getTo());
        if (connectionData != null) {
            MessageResponse messageResponse = new MessageResponse();
            messageResponse.getData().setFrom(from);
            messageResponse.getData().setContent(message.getData().getMessage());
            if (connectionData.getIpAddress().getHostAddress().equals(IPAddress.getHostAddress())
                    && connectionData.getPort() == port)
                return;
            String response = gson.toJson(messageResponse, MessageResponse.class);
            DatagramPacket sendPacket = new DatagramPacket(response.getBytes(),
                    response.length(),
                    connectionData.getIpAddress(),
                    connectionData.getPort());
            serverSocket.send(sendPacket);
        } else {
            ErrorResponse errorResponse = new ErrorResponse(NO_USER_WITH_NICKNAME);
            String response = gson.toJson(errorResponse, ErrorResponse.class);
            DatagramPacket sendPacket = new DatagramPacket(response.getBytes(),
                    response.length(),
                    IPAddress,
                    port);
            serverSocket.send(sendPacket);
        }
    }

    private static void listUsers(InetAddress IPAddress, int port, DatagramSocket serverSocket) throws IOException {
        ListUserResponse listUserResponse = new ListUserResponse();
        List<String> userNames = new ArrayList<>();
        String requestUser = getUser(IPAddress, port);
        ConnectionData connectionData = users.get(requestUser);

        for (Map.Entry<String, ConnectionData> user : users.entrySet()) {
            if (!user.getKey().equals(requestUser) && !connectionData.getBlockedUsers().contains(user.getKey()))
                userNames.add(user.getKey());
        }
        listUserResponse.getData().setUsers(userNames);
        String response;
        if (userNames.size() == 0) {
            ErrorResponse errorResponse = new ErrorResponse(NO_ONLINE_USERS);
            response = gson.toJson(errorResponse, ErrorResponse.class);

        } else
            response = gson.toJson(listUserResponse, ListUserResponse.class);

        DatagramPacket sendPacket = new DatagramPacket(response.getBytes(),
                response.length(),
                IPAddress,
                port);
        serverSocket.send(sendPacket);
    }

    private static void exit(InetAddress IPAddress, int port) {
        String user = getUser(IPAddress, port);
        if (user != null) {
            users.remove(user);
        }
    }

    private static void notValidRequestError(InetAddress IPAddress, int port, DatagramSocket serverSocket) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(NO_REQUEST);
        String responseError = gson.toJson(errorResponse, ErrorResponse.class);
        DatagramPacket sendPacket = new DatagramPacket(responseError.getBytes(),
                responseError.length(),
                IPAddress,
                port);
        serverSocket.send(sendPacket);
    }

    private static void sendFilePackage(InetAddress IPAddress, int port, DatagramSocket serverSocket, DatagramPacket datagramPacket) throws IOException {
        byte[] file_package = Arrays.copyOfRange(datagramPacket.getData(), 0, datagramPacket.getLength());
        DatagramPacket sendPacket = new DatagramPacket(file_package,
                file_package.length,
                IPAddress,
                port);
        serverSocket.send(sendPacket);
    }

    private static void registerFileRequest(String from, InetAddress IPAddress, int port, DatagramSocket serverSocket, String request) throws IOException {

        SendFile sendFile = gson.fromJson(request, SendFile.class);
        String to = sendFile.getData().getReceiver();
        if (from == null)
            return;

        if(users.get(from).getBlockedUsers().contains(to))
            return;

        if (! users.containsKey(sendFile.getData().getReceiver())) {
            ErrorResponse errorResponse = new ErrorResponse(NO_USER_WITH_NICKNAME);
            String response = gson.toJson(errorResponse, ErrorResponse.class);
            DatagramPacket sendPacket = new DatagramPacket(response.getBytes(),
                    response.length(),
                    IPAddress,
                    port);
            serverSocket.send(sendPacket);
            return;
        }
        ConnectionData connectionData = users.get(from);
        connectionData.setSending_File(true);
        connectionData.setFile_receiver(sendFile.getData().getReceiver());
        ConnectionData receiver = users.get(sendFile.getData().getReceiver());
        SendFileResponse sendFileResponse = new SendFileResponse(sendFile.getData().getName());
        String response = gson.toJson(sendFileResponse, SendFileResponse.class);
        DatagramPacket sendPacket = new DatagramPacket(response.getBytes(),
                response.length(),
                IPAddress,
                port);
        serverSocket.send(sendPacket);

        String fileName = sendFile.getData().getName();
        ReceiveFileResponse receiveFileResponse = new ReceiveFileResponse(from, fileName.substring(0, fileName.indexOf(".")) + "1" + fileName.substring(fileName.indexOf(".")));
        response = gson.toJson(receiveFileResponse, ReceiveFileResponse.class);
        sendPacket = new DatagramPacket(response.getBytes(),
                response.length(),
                receiver.getIpAddress(),
                receiver.getPort());
        serverSocket.send(sendPacket);
    }

    private static void fileSent(String from) {
        ConnectionData connectionData = users.get(from);
        connectionData.setFile_receiver("");
        connectionData.setSending_File(false);
    }

    private static void blockUser(String request, InetAddress IPAddress, int port, DatagramSocket serverSocket) throws IOException {
        BlockUser blockUser = gson.fromJson(request, BlockUser.class);
        String blockedUser = blockUser.getData().getUser();
        String requestUser = getUser(IPAddress, port);

        if(users.containsKey(blockUser.getData().getUser()) && (!blockedUser.equals(requestUser)))
        {
            users.get(requestUser).getBlockedUsers().add(blockedUser);
            users.get(blockedUser).getBlockedUsers().add(requestUser);
            OkResponse okResponse = new OkResponse("user_blocked");
            sendDatagram(okResponse, IPAddress, port, serverSocket);

        }
        else
        {
            ErrorResponse errorResponse = new ErrorResponse("Error, el usuario \"" + blockedUser + "\" no pudo ser bloqueado");
            sendDatagram(errorResponse, IPAddress, port, serverSocket);
        }
    }

    private static void changeIP() {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = myIP;
            byte[] sendData = IPAddress.getHostAddress().getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 12345);
            clientSocket.send(sendPacket);
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void sendDatagram(Object object, InetAddress IPAddress, int port, DatagramSocket serverSocket) throws IOException {
        String message = gson.toJson(object, object.getClass());
        DatagramPacket sendPacket = new DatagramPacket(
                message.getBytes(),
                message.length(),
                IPAddress,
                port);
        serverSocket.send(sendPacket);
    }

    private static class Listener extends Thread {
        @Override
        public void run() {
            try {
                DatagramSocket datagramSocket = new DatagramSocket(SERVER_PORT);
                byte[] receiveData = new byte[1024];
                while(true)
                {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    datagramSocket.receive(receivePacket);
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
                            sendDatagram(ping, IPAddress, port, datagramSocket);
                            break;
                        case VOTE:
                            OkResponse okResponse = new OkResponse();
                            sendDatagram(okResponse, IPAddress, port, datagramSocket);
                            voting();
                            break;
                        case COORDINATOR:
                            CoordinatorResponse response = gson.fromJson(request, CoordinatorResponse.class);
                            coordinator = response.getCoordinator();
                            startPings();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private static void voting() {
        ArrayList<String> bullys = getBullies();
        int size = bullys.size();

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
                    DatagramSocket socket = new DatagramSocket(SERVER_PORT);
                    Vote vote = new Vote();
                    String request = gson.toJson(vote, Vote.class);
                    DatagramPacket sendPacket = new DatagramPacket(request.getBytes(), request.length(), realAddress, SERVER_PORT);
                    socket.send(sendPacket);

                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);
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
        for(Future<Boolean> future : futures) {
            try {
                responded |= future.get();
            } catch (Exception e) {
                responded = false;
            }
        }
        executorService.shutdown();

        if (!responded) {
            changeIP();
            sendCoordinator();
        }
    }

    private static ArrayList<String> getBullies() {
        ArrayList<String> bullies = new ArrayList<>();
        String address = myIP.getHostAddress();
        for(String bully : servers) {
            if (address.compareTo(bully) < 0)
                bullies.add(bully);
        }
        return bullies;
    }

    private static void sendCoordinator() {
        try {
            DatagramSocket datagramSocket = new DatagramSocket(SERVER_PORT);
            InetAddress IPAddress;
            CoordinatorResponse coordinatorResponse;
            for(String server: servers) {
                if (myIP.getHostAddress().equals(server))
                    continue;
                IPAddress = InetAddress.getByName(server);
                coordinatorResponse = new CoordinatorResponse(server);
                sendDatagram(coordinatorResponse, IPAddress, SERVER_PORT, datagramSocket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startPings() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        try {
                            String myAddress = myIP.getHostAddress();
                            if (myAddress.equals(coordinator))
                                return;
                            System.out.println("Enviando ping al coordinador " + coordinator);
                            DatagramSocket datagramSocket = new DatagramSocket(SERVER_PORT);
                            byte[] receiveData = new byte[1024];
                            DatagramPacket datagramPacket = new DatagramPacket(receiveData, receiveData.length);
                            datagramSocket.setSoTimeout(2000);
                            Ping ping = new Ping();
                            InetAddress address = InetAddress.getByName(coordinator);
                            sendDatagram(ping, address, SERVER_PORT, datagramSocket);

                            datagramSocket.receive(datagramPacket);
                            datagramSocket.close();
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (SocketTimeoutException e) {
                            //TODO proceso de votacion
                            voting();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                },
                0,
                5000
        );
    }

    private static InetAddress getMyIPAddress() {
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
                        return i;
                    }
                }
            }
        }
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
        return null;
    }
}
