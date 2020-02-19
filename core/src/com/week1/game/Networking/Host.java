package com.week1.game.Networking;

import com.badlogic.gdx.Gdx;
import com.week1.game.Networking.Messages.Control.PlayerIdMessage;
import com.week1.game.Networking.Messages.Game.CheckSyncMessage;
import com.week1.game.Networking.Messages.Game.GameMessage;
import com.week1.game.Networking.Messages.Game.InitMessage;
import com.week1.game.Networking.Messages.Game.SyncIssueMessage;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.Messages.Update;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.week1.game.Networking.Messages.MessageType.SYNCERR;

public class Host {

    private static final String TAG = "Host - lji1";
    private static final int UPDATE_INTERVAL = 200;
    private int port;
    private DatagramSocket udpSocket;
    
    private Map<InetAddress, Player> registry = new HashMap<>();
    
    private boolean gameStarted = false;
    private StringBuilder aggregateMessage = new StringBuilder();
    
    private ConcurrentLinkedQueue<String> incomingMessages = new ConcurrentLinkedQueue<>();
    
    
    public Host(int port) throws IOException {
        this.udpSocket = new DatagramSocket(port);
        udpSocket.setReuseAddress(true);
//        this.port = udpSocket.getLocalPort();
        this.port = port;
        
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
        // TODO: implement
        // every interval:
        // - empty the incoming message queue
        // - package the messages together into an update
        // - broadcast them to all registered players
        
        // spawn a new thread to broadcast updates to the registered clients
        Gdx.app.log(TAG, "Host is about to begin running update loop.");
        new Thread(() -> {
            while (true) {
                List<String> outgoingMessages = new ArrayList<>();
                while (!incomingMessages.isEmpty()) { // TODO: dangerous, if many messages coming all at once
                    outgoingMessages.add(incomingMessages.poll());
                }

                // Verify game state on any messages sent this game turn
                int prevHash = 0; // This will be the hash that everything is compared to
                boolean didFail = false;
                for (int i = 0; i < outgoingMessages.size(); i++) {
                    GameMessage msg = MessageFormatter.parseMessage(outgoingMessages.get(i));
                    if (msg != null && msg instanceof CheckSyncMessage) {
                        int hash = msg.getHashCode();
                        if (prevHash == 0) {
                            // if it has not been set, set the standard to this one.
                            prevHash = hash;
                            continue;
                        } else {
                            if (prevHash != hash) {
                                Gdx.app.log("pjb3 - Host", "ERROR: The hashes do not match for two messages!!!!! Yikes.");
                                // Create a SyncIssue message to send to all clients so they can know there is an issue
                                didFail = true;
                            }
                        }

                    }
                }
                if (didFail) {
                    outgoingMessages.add(0, MessageFormatter.packageMessage(new SyncIssueMessage(-1, SYNCERR, prevHash)));
                } else {
                    Gdx.app.log("pjb3 - Host", "Nice. The hashes match up.");
                }


                Gdx.app.debug(TAG, "Host is about to broadcast update message to registered clients.");
                broadcastToRegisteredPlayers(MessageFormatter.packageMessage(new Update(outgoingMessages)));
                
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
                try {
                    Thread.sleep(2000);
                }  catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ;
                // TODO: this gets sent first so that the game engine does any initialization before the game starts (but udp doesn't guarantee order)
                broadcastToRegisteredPlayers(MessageFormatter.packageMessage(
                        new Update(Arrays.asList(new String[] {MessageFormatter.packageMessage(new InitMessage(registry.size(), -1, 0))}))));
                
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
