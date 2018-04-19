package mx.iteso.distribuidos;

import com.google.gson.Gson;
import mx.iteso.distribuidos.requests.BaseRequest;
import mx.iteso.distribuidos.requests.Message;
import mx.iteso.distribuidos.requests.SetUser;
import mx.iteso.distribuidos.response.ErrorResponse;
import mx.iteso.distribuidos.utils.ConnectionData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

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
            byte[] sendData = new byte[1024];

            while(true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                String sentence = new String(receivePacket.getData());
                Gson gson = new Gson();
                BaseRequest baseRequest = gson.fromJson(sentence, BaseRequest.class);

                switch (baseRequest.getType()) {
                    case SET_NAME:
                        SetUser setUser = gson.fromJson(Arrays.toString(receivePacket.getData()), SetUser.class);
                        if (!users.containsKey(setUser.getData().getContent())) {
                            users.put(setUser.getData().getContent(), new ConnectionData(IPAddress, port));
                            DatagramPacket sendData = new DatagramPacket()
                        }
                        break;
                    case SEND_MESSAGE:
                        Message message = gson.fromJson(sentence, Message.class);
                        ConnectionData connectionData = users.get(message.getData().getTo());
                        if (connectionData != null) {
                            sendPacket =
                                    new DatagramPacket(message.getData().getMessage().getBytes(),
                                            message.getData().getMessage().length(),
                                            connectionData.getIpAddress(),
                                            connectionData.getPort());
                            serverSocket.send(sendPacket);
                        }
                        break;
                    case LIST_USERS:
                        String[] listUsers = new String[users.size()];
                        Set<String> keys = users.keySet();
                        int i = 0;
                        for(String key : keys)  {
                            listUsers[i++] = key;
                        }
                        break;
                    case EXIT:
                        break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
