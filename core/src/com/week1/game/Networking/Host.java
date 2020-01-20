package com.week1.game.Networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class Host {
    
    private int port;
    private DatagramSocket udpSocket;
    
    private Map<InetAddress, Player> registry = new HashMap<>();
    
    private boolean gameStarted = false;
    private StringBuilder aggregateMessage = new StringBuilder();
    
    
    public Host() throws SocketException, IOException {
        this.udpSocket = new DatagramSocket();
        this.port = udpSocket.getLocalPort();
        
        System.out.println("Creating socket for host instance with address: " +
                NetworkUtils.getLocalHostAddr() + " on port: " + this.port);
        
        
    }
    
    public int getPort() {
        return port;
    }

    public void listenForClientMessages() throws Exception {
        
        // Spawn a new thread to listen for messages incoming to the host
        new Thread(() -> {
            while (true) {
                System.out.println("Host is listening for next client message.");
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
//             Game has started
            if (msg.equals("join")) {
                System.out.println("Host received a 'join' message from: " + packet.getAddress().getHostAddress());
                registry.put(packet.getAddress(), new Player(packet.getAddress(), packet.getPort()));

                System.out.println("List of Players: ");
                registry.values().forEach((p) -> System.out.println("\t" + p.address + " : " + p.port));
            } else if (msg.equals("start")) {
                gameStarted = true;
                System.out.println("Host received a 'start' message from: " + packet.getAddress().getHostAddress());
                System.out.println("Host will forward the start message to all registered players.");
                broadcastToRegisteredPlayers("start");
            } else {
                System.out.println("Unrecognized message before start of game.");
            }
        } else {
            // Game has started
            System.out.println("Host received an update message from: " + packet.getAddress().getHostAddress());

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
                System.out.println("Failed to send message to: " + player.address);
                e.printStackTrace();
            }
        });
    }
}
