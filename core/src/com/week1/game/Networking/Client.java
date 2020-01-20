package com.week1.game.Networking;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    
    private static final String TAG = "Client - lji1";
    private DatagramSocket udpSocket;
    private InetAddress hostAddress;
    private int hostPort;
    private INetworkClientToEngineAdapter adapter;
    
    public Client(String hostIpAddr, int hostPort, INetworkClientToEngineAdapter adapter) throws IOException {
        this.hostAddress = InetAddress.getByName(hostIpAddr);
        this.hostPort = hostPort;
        this.adapter = adapter;
        
        this.udpSocket = new DatagramSocket();
        Gdx.app.log(TAG, "Created socket for client instance on port: " + udpSocket.getLocalPort());
        
        Gdx.app.log(TAG, "Sending join message.");
        sendMessage("join");
    }
    
    public void sendMessage(String msg) {
        DatagramPacket p = new DatagramPacket(
                msg.getBytes(), msg.getBytes().length, hostAddress, this.hostPort);

        Gdx.app.log(TAG, "About to send message: " + msg + " to: " + hostAddress + ":" + this.hostPort);
        try {
            this.udpSocket.send(p);
            Gdx.app.log(TAG, "Sent message");
        } catch (IOException e) {
            Gdx.app.error(TAG, "Failed to send message: " + msg);
        }
        
    }
    
    
    // TODO: Needs to be updated to reflect changed expectations -> probably calls deliverUpdate on the adapter
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
