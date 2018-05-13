import java.io.*;
import java.net.*;

public class Server {
	public static void main(String[] args) {
		try {
			DatagramSocket client = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName("localhost");
			String newIP = "128.0.0.1";
			//String newPort = "9999";
			byte[] sendData = newIP.getBytes();//newIP.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 1235);
			client.send(sendPacket);
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}