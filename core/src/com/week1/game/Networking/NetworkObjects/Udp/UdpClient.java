package com.week1.game.Networking.NetworkObjects.Udp;

import com.badlogic.gdx.Gdx;
import com.week1.game.Networking.INetworkClientToEngineAdapter;
import com.week1.game.Networking.Messages.*;
import com.week1.game.Networking.Messages.Control.ClientControlMessage;
import com.week1.game.Networking.Messages.Control.UdpJoinMessage;
import com.week1.game.Networking.Messages.Control.StartMessage;
import com.week1.game.Networking.Messages.Game.GameMessage;
import com.week1.game.Networking.NetworkObjects.AClient;
import com.week1.game.TowerBuilder.BlockSpec;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class UdpClient extends AClient {
    private static final String TAG = "Client - lji1";
    private DatagramSocket udpSocket;
    private InetAddress hostAddress;
    private int hostPort;
    private INetworkClientToEngineAdapter adapter;
    
    private int playerId = -1;
    
    
    public UdpClient(String hostIpAddr, int hostPort, INetworkClientToEngineAdapter adapter) throws IOException {
        this.hostAddress = InetAddress.getByName(hostIpAddr);
        this.hostPort = hostPort;
        this.adapter = adapter;
        
        this.udpSocket = new DatagramSocket();
        Gdx.app.log(TAG, "Created socket for client instance on port: " + udpSocket.getLocalPort());
        
        awaitUpdates();
    }

    public String getHostAddr() {
        return hostAddress.toString() + ":" + hostPort;
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
                byte[] buf = new byte[UdpHost.DANGEROUS_HARDCODED_MESSAGE_SIZE]; // TODO: size this according to message length
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                try {
                    // blocks until a packet is received
                    udpSocket.receive(packet);
                    String messages = new String(packet.getData()).trim();
                    
                    Gdx.app.log(TAG, "About to try parsing message: " + messages);
                    // try parsing as a control message first
                    ClientControlMessage controlMsg = MessageFormatter.parseClientControlMessage(messages);
                    if (controlMsg != null) {
                        Gdx.app.log(TAG, "Received control message: " + controlMsg);
                        controlMsg.updateClient(this);
                        continue; // don't need to try parsing as game messages if already successfully parsed as control message
                    }
                    
                    Gdx.app.debug(TAG, "Received update: " + messages);
                    List<GameMessage> msgList = MessageFormatter.parseMessage(messages);
                    adapter.deliverUpdate(msgList); 

                } catch (IOException e) {
                    e.printStackTrace();
                    Gdx.app.error(TAG, "Failed to receive update messages.");
                }
            }
        }).start();
    }
    
    
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
        adapter.setPlayerId(playerId);
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void sendStartMessage() {
        System.out.println("Trying to send start message.");
        // the client doesn't know its player id until later, so just use -1
        this.sendStringMessage(MessageFormatter.packageMessage(new StartMessage(-1)));
    }
    
    public void sendJoinMessage(List<List<BlockSpec>> details) {
        Gdx.app.log(TAG, "Sending join message.");
        // the client doesn't know its player id until later, so just use -1
        sendStringMessage(MessageFormatter.packageMessage(new UdpJoinMessage(-1, details)));
    }
}
