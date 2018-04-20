package mx.iteso.distribuidos;

import com.google.gson.Gson;
import mx.iteso.distribuidos.requests.BaseRequest;
import mx.iteso.distribuidos.requests.Exit;
import mx.iteso.distribuidos.requests.Message;
import mx.iteso.distribuidos.requests.SetUser;
import mx.iteso.distribuidos.response.ErrorResponse;
import mx.iteso.distribuidos.response.ListUserResponse;
import mx.iteso.distribuidos.response.MessageResponse;
import mx.iteso.distribuidos.response.OkResponse;
import mx.iteso.distribuidos.utils.ConnectionData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

import static mx.iteso.distribuidos.utils.Constants.*;

/**
 * Hello world!
 *
 */
public class App 
{
    private static Map<String, ConnectionData> users;

    public static void main( String[] args ) {

        try {
            DatagramSocket serverSocket = new DatagramSocket(PORT);
            byte[] receiveData = new byte[1024];

            while(true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                String sentence = new String(receivePacket.getData());
                Gson gson = new Gson();
                BaseRequest baseRequest = gson.fromJson(sentence, BaseRequest.class);

                switch (baseRequest.getType()) {
                    case SET_NAME: {
                        SetUser setUser = gson.fromJson(Arrays.toString(receivePacket.getData()), SetUser.class);
                        if (!users.containsKey(setUser.getData().getContent())) {
                            users.put(setUser.getData().getContent(), new ConnectionData(IPAddress, port));
                            OkResponse ok = new OkResponse();
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
                        break;
                    }
                    case SEND_MESSAGE: {
                        Message message = gson.fromJson(sentence, Message.class);
                        String from = getUser(IPAddress, port);
                        if (from == null)
                            return;
                        if (message.getData().getTo().equals("all")) {
                            DatagramPacket sendPacket;
                            for (Map.Entry<String, ConnectionData> entry : users.entrySet()) {
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
                        }
                        break;
                    }
                    case LIST_USERS: {
                        ListUserResponse listUserResponse = new ListUserResponse();
                        List<String> userNames = new ArrayList<>();
                        for (Map.Entry<String, ConnectionData> user : users.entrySet()) {
                            userNames.add(user.getKey());
                        }
                        listUserResponse.getData().setUsers(userNames);
                        String response = gson.toJson(listUserResponse, ListUserResponse.class);
                        DatagramPacket sendPacket = new DatagramPacket(response.getBytes(),
                                response.length(),
                                IPAddress,
                                port);
                        serverSocket.send(sendPacket);
                        break;
                    }
                    case EXIT: {
                        String user = getUser(IPAddress, port);
                        if (user != null) {
                            users.remove(user);
                        }
                        break;
                    }
                    default:
                        ErrorResponse errorResponse = new ErrorResponse(NO_REQUEST);
                        String responseError = gson.toJson(errorResponse, ErrorResponse.class);
                        DatagramPacket sendPacket = new DatagramPacket(responseError.getBytes(),
                                responseError.length(),
                                IPAddress,
                                port);
                        serverSocket.send(sendPacket);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getUser(InetAddress IPAddress, int port) {
        ConnectionData search = new ConnectionData(IPAddress, port);
        for(Map.Entry<String, ConnectionData> user: users.entrySet()) {
            if (user.equals(search)){
                return user.getKey();
            }
        }
        return null;
    }
}
