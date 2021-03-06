package mx.iteso.distribuidos;

import com.google.gson.Gson;
import mx.iteso.distribuidos.requests.*;
import mx.iteso.distribuidos.response.*;
import mx.iteso.distribuidos.utils.ConnectionData;
import sun.plugin.dom.core.CoreConstants;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import static mx.iteso.distribuidos.utils.Constants.*;

public class App implements ChangeCoordinatorListener {
    private static Map<String, ConnectionData> users = new HashMap<>();
    private static Gson gson = new Gson();
    private static InetAddress myIP;
    private static String coordinator = "192.168.1.2";
    private static ArrayList<String> servers = new ArrayList<String>(){{add("192.168.1.2");add("192.168.1.3");/*add("192.168.1.4");*/}};
    private static DatagramSocket serversSocket;
    private static DatagramSocket clientSocket;
    private static DatagramSocket ipSocket;

    public static void main( String[] args ) {
        try {
            serversSocket = new DatagramSocket(SERVER_PORT);
            clientSocket = new DatagramSocket(CLIENT_PORT);
            ipSocket = new DatagramSocket();
            myIP = getMyIPAddress();
            listener = new Listener();
            listener.start();
            voting();
            byte[] receiveData = new byte[1024];
            while(true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
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
                        sendFilePackage(receiver.getIpAddress(), receiver.getPort(), clientSocket, receivePacket);
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
                    clientSocket.send(sendPacket);
                    continue;
                }

                System.out.println(request + CLIENT_PORT);

                switch (baseRequest.getType()) {
                    case SET_NAME:
                        setName(request, IPAddress, port, clientSocket);
                        break;
                    case SEND_MESSAGE:
                        sendMessage(request, IPAddress, port, clientSocket);
                        break;
                    case LIST_USERS:
                        listUsers(IPAddress, port, clientSocket);
                        break;
                    case SEND_FILE:
                        registerFileRequest(from, IPAddress, port, clientSocket, request);
                        break;
                    case FILE_SENT:
                        fileSent(from);
                        break;
                    case BLOCK_USER:
                        blockUser(request, IPAddress, port, clientSocket);
                        break;
                    case EXIT:
                        exit(IPAddress, port);
                        break;
                    default:
                        notValidRequestError(IPAddress, port, clientSocket);
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
            ConnectionData connectionData = new ConnectionData(IPAddress, port);
            users.put(setUser.getData().getContent(), connectionData);
            SetNameResponse setNameResponse = new SetNameResponse(setUser.getData().getContent(), connectionData);
            sendNewName(setNameResponse);
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
            sendExitName(user);
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
    }
}
