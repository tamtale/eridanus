package com.week1.game.Networking;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class Host {

    private static final String TAG = "Host - lji1";
    private int port;
    private DatagramSocket udpSocket;
    
    private Map<InetAddress, Player> registry = new HashMap<>();
    
    private boolean gameStarted = false;
    private StringBuilder aggregateMessage = new StringBuilder();
    
    
    public Host() throws IOException {
        this.udpSocket = new DatagramSocket();
        this.port = udpSocket.getLocalPort();
        
        Gdx.app.log(TAG, "Creating socket for host instance with address: " +
                NetworkUtils.getLocalHostAddr() + " on port: " + this.port);
        
    }
    
    public int getPort() {
        return port;
    }

    public void listenForClientMessages() {
        
        // Spawn a new thread to listen for messages incoming to the host
        new Thread(() -> {
            while (true) {
                Gdx.app.log(TAG, "Host is listening for next client message.");
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                try {
                    // blocks until a packet is received
                    udpSocket.receive(packet);
                    processMessage(packet);
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    private void processMessage(DatagramPacket packet) {
        String msg = new String(packet.getData()).trim();
        
        if (!gameStarted) {
//             Game has not started
            if (msg.equals("join")) {
                Gdx.app.log(TAG, "Host received a 'join' message from: " + packet.getAddress().getHostAddress());
                registry.put(packet.getAddress(), new Player(packet.getAddress(), packet.getPort()));

                Gdx.app.log(TAG, "List of Players: ");
                registry.values().forEach((p) -> Gdx.app.log(TAG, "\t" + p.address + " : " + p.port));
            } else if (msg.equals("start")) {
                gameStarted = true;
                Gdx.app.log(TAG, "Host received a 'start' message from: " + packet.getAddress().getHostAddress() + 
                        "\nHost will forward the start message to all registered players.");
                broadcastToRegisteredPlayers("start");
            } else {
                Gdx.app.error(TAG, "Unrecognized message before start of game.");
            }
        } else {
            // Game has started
            Gdx.app.log(TAG, "Host received an update message from: " + packet.getAddress().getHostAddress());

            aggregateMessage.append(msg);
            registry.get(packet.getAddress()).checkedIn = true;

            // check if all players have checked in yet
            boolean ready = true;
            for (Player player : registry.values()) {
                ready = ready && player.checkedIn;
            }

            if (ready) {
                // all players have checked in; send out the next update
                broadcastToRegisteredPlayers(aggregateMessage.toString());

                // clean up for the next round
                aggregateMessage = new StringBuilder();
                registry.values().forEach((player) -> player.checkedIn = false);
            } 
            // else, wait for the other players to check in
        }
        
        
    }
    
    private void broadcastToRegisteredPlayers(String msg) {
        registry.values().forEach((player) -> {
            DatagramPacket p = new DatagramPacket(msg.getBytes(), msg.getBytes().length, player.address, player.port);
            try {
                this.udpSocket.send(p);
            } catch (IOException e) {
                Gdx.app.error(TAG, "Failed to send message to: " + player.address);
                e.printStackTrace();
            }
        });
    }
}
