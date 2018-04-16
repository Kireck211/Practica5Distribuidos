package mx.iteso.distribuidos;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

/**
 * Hello world!
 *
 */
public class App 
{
    public static int PORT = 1234;

    public static void main( String[] args ) {

        try {
            DatagramSocket serverSocket = new DatagramSocket(PORT);
            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];

            while(true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                int length = receivePacket.getLength();
                String sentence = new String(receivePacket.getData());
                sentence = sentence.substring(0, length);
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                System.out.println("Received: " + sentence + ", from :" + IPAddress.getHostAddress() + ", port: " + port);
                String capitalizedSentence = sentence.toUpperCase();
                sendData = capitalizedSentence.getBytes();
                DatagramPacket sendPacket =
                        new DatagramPacket(sendData, sendData.length, IPAddress, port);

                serverSocket.send(sendPacket);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
