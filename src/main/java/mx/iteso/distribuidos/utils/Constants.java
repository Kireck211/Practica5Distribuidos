package mx.iteso.distribuidos.utils;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Constants {
    public static final String SET_NAME = "set_name";
    public static final String SEND_MESSAGE = "send_message";
    public static final String EXIT = "exit";
    public static final String LIST_USERS = "list_users";
    public static final String SEND_FILE = "send_file";
    public static final String RECEIVE_FILE = "receive_file";
    public static final String BLOCK_USER = "block_user";
    public static final String FILE_SENT = "file_sent";
    public static final String RESPONSE = "response";
    public static final String MESSAGE_RECEIVED = "message_received";
    public static final int RESPONSE_ERROR_CODE = 500;
    public static final String USER_TAKEN = "Sorry, user already taken, try again please.";
    public static final String NO_REQUEST = "Error on request";
    public static final String NO_USER_WITH_NICKNAME ="Sorry, could not find a user with that nickname, try again please.";
    public static final String NOT_REGISTERED = "Please register your user to perform this action";
    public static final String NO_ONLINE_USERS = "No online users";
    public static final int CLIENT_PORT = 1234;
    public static final int IP_PORT = 1235;
    public static final int SERVER_VOTE = 1236;
    public static final int SERVER_PING = 1237;
    public static final int SERVER_COORDINATOR = 1238;
    public static final String PING = "ping";
    public static final String VOTE = "vote";
    public static final String COORDINATOR= "coordinator";
    public static final String REMOVE_CLIENT = "remove_client";

    public static void sendDatagram(Object object, InetAddress IPAddress, int port, DatagramSocket serverSocket) throws IOException {
        Gson gson = new Gson();
        String message = gson.toJson(object, object.getClass());
        DatagramPacket sendPacket = new DatagramPacket(
                message.getBytes(),
                message.length(),
                IPAddress,
                port);
        serverSocket.send(sendPacket);
    }
}
