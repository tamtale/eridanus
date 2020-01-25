package com.week1.game.Networking;

import com.badlogic.gdx.Gdx;
import com.week1.game.Networking.Messages.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Host {

    private static final String TAG = "Host - lji1";
    private static final int UPDATE_INTERVAL = 3000;
    private int port;
    private DatagramSocket udpSocket;
    
    private Map<InetAddress, Player> registry = new HashMap<>();
    
    private boolean gameStarted = false;
    
    private ConcurrentLinkedQueue<String> incomingMessages = new ConcurrentLinkedQueue<>();
    
    
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
                byte[] buf = new byte[1024]; // TODO: DANGEROUS HARDCODED
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
    
    private void runUpdateLoop() {
        // spawn a new thread to broadcast updates to the registered clients
        Gdx.app.log(TAG, "Host is about to begin running update loop.");
        new Thread(() -> {
            while (true) {
                List<String> outgoingMessages = new ArrayList<>();
                while (!incomingMessages.isEmpty()) { // TODO: dangerous, if many messages coming all at once
//                    Gdx.app.log(TAG, "queue is non empty");
                    outgoingMessages.add(incomingMessages.poll());
                }

                Gdx.app.log(TAG, "Host is about to broadcast update message to registered clients.");
                broadcastToRegisteredPlayers(MessageFormatter.packageMessage(new Update(outgoingMessages)));
                
//                broadcastToRegisteredPlayers(MessageFormatter.packageMessage(new Update(Arrays.asList(
//                        MessageFormatter.packageMessage(new CreateMinionMessage(
//                                ThreadLocalRandom.current().nextInt(20, 160),
//                                ThreadLocalRandom.current().nextInt(20, 160),
//                                69, 
//                                420))
////                        MessageFormatter.packageMessage(new TestMessage(345345, "omgwow", 10000))
////                        MessageFormatter.packageMessage(new CreateMinionMessage(111, 999, 555, 666))
//                ))));
                
                // Take a break before the next update
                try { Thread.sleep(UPDATE_INTERVAL); } catch (InterruptedException e) {e.printStackTrace();}

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
                Gdx.app.log(TAG, "Host received a 'start' message from: " + packet.getAddress().getHostAddress());
                
                // tell each player what their id is
                int playerId = 0;
                for (Player player : registry.values()) {
                    String playerIdMessage = MessageFormatter.packageMessage(new PlayerIdMessage(playerId++));
                    DatagramPacket p = new DatagramPacket(playerIdMessage.getBytes(), playerIdMessage.getBytes().length, player.address, player.port);
                    try {
                        this.udpSocket.send(p);
                    } catch (IOException e) {
                        Gdx.app.error(TAG, "Failed to send message to: " + player.address);
                        e.printStackTrace();
                    }
                }
                
                runUpdateLoop();
                
            } else {
                Gdx.app.error(TAG, "Unrecognized message before start of game.");
            }
        } else {
            // Game has started
            Gdx.app.log(TAG, "Host received an update message from: " + packet.getAddress().getHostAddress());
            incomingMessages.add(msg);
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
