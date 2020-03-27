package com.week1.game.Networking.NetworkObjects;

import com.badlogic.gdx.Gdx;
import com.week1.game.Model.TowerLite;
import com.week1.game.Networking.Messages.Control.ClientControl.JoinedPlayersMessage;
import com.week1.game.Networking.Messages.Control.ClientControl.PlayerIdMessage;
import com.week1.game.Networking.Messages.Control.HostControl.HostControlMessage;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.Messages.Update;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.week1.game.Networking.Messages.MessageFormatter.parseHostControlMessage;


/**
 * This is the Host that receives and broadcasts messages to all of the clients.
 */
public class Host {

    private static final String TAG = "Host - lji1/pjb3";
    private static final int UPDATE_INTERVAL = 200;
    private int port;
    public ServerSocket serverSocket;
    private int nextPlayerId = 0;

    public Map<Integer, List<TowerLite>> towerDetails = new HashMap<>(); // first index is implicitly the player id
    public Map<InetAddress, Player> registry = new HashMap<>();

    public int runningPlayerId = 0;
    public boolean gameStarted = false;
    
    private ConcurrentLinkedQueue<String> incomingMessages = new ConcurrentLinkedQueue<>();
    
    public Host(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(port);
        
        Gdx.app.log(TAG, "Creating socket for host instance with address: " +
                NetworkUtils.getLocalHostAddr() + " on port: " + this.port);
        
    }
    
    public int getPort() {
        return port;
    }

    public void listenForClientMessages() {
        
        // Accepts connections to joining players
        Thread joiningPlayersThead = new Thread(() -> {
            while (true) {
                try {
                    Socket socket = this.serverSocket.accept();
                    Player player = new Player(
                            this.runningPlayerId++,
                            socket.getInetAddress(),
                            socket.getPort(),
                            socket.getInputStream(),
                            socket.getOutputStream()
                    );
                    registry.put(socket.getInetAddress(), player);

                    // Tell them their playerID
                    String playerIdMessage = MessageFormatter.packageMessage(new PlayerIdMessage(player.playerId));
                    sendMessage(playerIdMessage, player);
                    
                    // Tell everyone that someone has joined the game
                    List<String> joinedPlayers = new ArrayList<>();
                    registry.forEach((addr, plyr) -> joinedPlayers.add(plyr.playerId + ": " + addr.getHostAddress()));
                    java.util.Collections.sort(joinedPlayers);
                    broadcastToRegisteredPlayers(MessageFormatter.packageMessage(new JoinedPlayersMessage(-1, joinedPlayers)));

                    // spawn a thread to listen on this socket
                    new Thread(() -> {
                        String msg = "";
                        while (true) {
                            try {
                                Gdx.app.debug(TAG, "Host is listening for next client message from: "  + player.address);
                                msg = player.in.readLine();
                                processMessage(msg, player.address, player.port);


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        joiningPlayersThead.start(); 
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

                Gdx.app.debug(TAG, "Host is about to broadcast update message to registered clients.");
                broadcastToRegisteredPlayers(MessageFormatter.packageMessage(new Update(outgoingMessages)));
                
                // Take a break before the next update
                try { Thread.sleep(UPDATE_INTERVAL); } catch (InterruptedException e) {e.printStackTrace();}

            }
        }).start();
    }


    private void processMessage(String msg, InetAddress addr, int port) {

        HostControlMessage ctrlMsg = parseHostControlMessage(msg);
        if (ctrlMsg != null) {
            ctrlMsg.updateHost(this, addr, port);
            return;
        }

        Gdx.app.debug(TAG, "Host received an update message from: " + addr.getHostAddress());
        incomingMessages.add(msg);

    }
    
    public void broadcastToRegisteredPlayers(String msg) {
        registry.values().forEach((player) -> {
            Gdx.app.log(TAG, "Sending message: " + msg + " to player: " + player.address);
            sendMessage(msg, player);
        });
    }

    public void sendMessage(String msg, Player player) {
        try {
            player.out.write(msg + "\n");
            player.out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNextPlayerId() {
        return nextPlayerId;
    }
}
