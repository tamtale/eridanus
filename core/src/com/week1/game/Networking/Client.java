package demo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {
    
    private DatagramSocket udpSocket;
    private InetAddress hostAddress;
    private int hostPort;
//    private Scanner scanner;
    
    public Client(String hostIpAddr, int hostPort) throws IOException {
        System.out.println("hostIpAddr: " + hostIpAddr);
        this.hostAddress = InetAddress.getByName(hostIpAddr);
        System.out.println("hostAddress: " + this.hostAddress);
        this.hostPort = hostPort;
        
        this.udpSocket = new DatagramSocket();
        System.out.println("Created socket for client instance on port: " + udpSocket.getLocalPort());
        
        System.out.println("Sending join message.");
        sendMessage("join");
    }
    
    public void sendMessage(String msg) {
        // Send join message to host
        DatagramPacket p = new DatagramPacket(
                msg.getBytes(), msg.getBytes().length, hostAddress, this.hostPort);

        System.out.println("About to send message: " + msg + " to: " + hostAddress + ":" + this.hostPort);
        try {
            this.udpSocket.send(p);
            System.out.println("Sent");
        } catch (IOException e) {
            System.out.println("Failed to send message: " + msg);
        }
        
    }
    
    public String waitForUpdate() {
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        try {
            // blocks until a packet is received
            udpSocket.receive(packet);
            return new String(packet.getData()).trim();

        } catch (IOException e) {
            e.printStackTrace();
            return "Failure to receive update.";
        }
    }
    
   // TODO: Write update for  Monday! 
    
    
    
}
