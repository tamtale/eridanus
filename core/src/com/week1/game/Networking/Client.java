package com.week1.game.Networking;

import com.badlogic.gdx.Gdx;
import com.week1.game.Networking.Messages.AMessage;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.Messages.Update;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client {
    
    private static final String TAG = "Client - lji1";
    private DatagramSocket udpSocket;
    private InetAddress hostAddress;
    private int hostPort;
    private INetworkClientToEngineAdapter adapter;
    
    private ConcurrentLinkedQueue<AMessage> messagesToSend = new ConcurrentLinkedQueue<>();
    
    public Client(String hostIpAddr, int hostPort, INetworkClientToEngineAdapter adapter) throws IOException {
        this.hostAddress = InetAddress.getByName(hostIpAddr);
        this.hostPort = hostPort;
        this.adapter = adapter;
        
        this.udpSocket = new DatagramSocket();
        Gdx.app.log(TAG, "Created socket for client instance on port: " + udpSocket.getLocalPort());
        
        Gdx.app.log(TAG, "Sending join message.");
        sendStringMessage("join");
        
        awaitUpdates();
    }
    
    public void sendMessage(AMessage msg) {
        messagesToSend.add(msg);
    }
    
    // TODO: since using UDP protocol, doesn't guarantee ordering of messages -> update to TCP to resolve
    public void sendStringMessage(String msg) {
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
    
    
    public void awaitUpdates() {
        
        new Thread(() -> {
            while (true) {
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                try {
                    // blocks until a packet is received
                    udpSocket.receive(packet);
                    String messages = new String(packet.getData()).trim();
                    Gdx.app.log(TAG, "Received update: " + messages);
                    List<AMessage> msgList = MessageFormatter.parseMessage(messages);
                    adapter.deliverUpdate(msgList); 

                } catch (IOException e) {
                    e.printStackTrace();
                    Gdx.app.error(TAG, "Failed to receive update messages.");
                }
            }
        }).start();
    }
    
   // TODO: Write update for  Monday! 
    
    
    
}
