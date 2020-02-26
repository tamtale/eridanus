package com.week1.game.Networking;

import com.badlogic.gdx.Gdx;
import com.week1.game.Networking.Messages.Control.HostControlMessage;
import com.week1.game.Networking.Messages.Game.CheckSyncMessage;
import com.week1.game.Networking.Messages.Game.GameMessage;
import com.week1.game.Networking.Messages.Game.SyncIssueMessage;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.Messages.Update;
import com.week1.game.TowerBuilder.BlockSpec;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.week1.game.Networking.Messages.MessageFormatter.parseHostControlMessage;
import static com.week1.game.Networking.Messages.MessageType.SYNCERR;

public class Host {
    public static final int DANGEROUS_HARDCODED_MESSAGE_SIZE = 10000;

    private static final String TAG = "Host - lji1";
    private static final int UPDATE_INTERVAL = 200;
    private int port;
    public DatagramSocket udpSocket;
    
    public Map<InetAddress, Player> registry = new HashMap<>();
    
    public boolean gameStarted = false;
//    private StringBuilder aggregateMessage = new StringBuilder();
    
    private ConcurrentLinkedQueue<String> incomingMessages = new ConcurrentLinkedQueue<>();
    
    public List<List<List<BlockSpec>>> towerDetails = new ArrayList<>(); // first index is implicitly the player id
    public int runningPlayerId = 0;
    
    
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
                byte[] buf = new byte[DANGEROUS_HARDCODED_MESSAGE_SIZE]; // TODO: DANGEROUS HARDCODED
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
    
    public void runUpdateLoop() {
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
                boolean didCheck = false;
                for (String outgoingMessage : outgoingMessages) {
                    GameMessage msg = MessageFormatter.parseMessage(outgoingMessage);
                    if (msg instanceof CheckSyncMessage) {
                        didCheck = true;
                        int hash = msg.getHashCode();
                        if (prevHash == 0) {
                            // if it has not been set, set the standard to this one.
                            prevHash = hash;
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
                } else if (didCheck) {
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
            // Game has not started
            HostControlMessage ctrlMsg = parseHostControlMessage(msg);
            if (ctrlMsg != null) {
                ctrlMsg.updateHost(this, packet);
            } else {
                Gdx.app.error(TAG, "Unrecognized message before start of game.");
            }
        } else {
            // Game has started
            Gdx.app.log(TAG, "Host received an update message from: " + packet.getAddress().getHostAddress());
            incomingMessages.add(msg);
        }
    }
    
    public void broadcastToRegisteredPlayers(String msg) {
        registry.values().forEach((player) -> {
            System.out.println("Sending message: " + msg + " to player: " + player.address);
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
