package com.week1.game.Networking.NetworkObjects.Tcp;

import com.badlogic.gdx.Gdx;
import com.week1.game.Networking.Messages.Control.HostControlMessage;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.Messages.Update;
import com.week1.game.Networking.NetworkObjects.AHost;
import com.week1.game.Networking.NetworkObjects.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.week1.game.Networking.Messages.MessageFormatter.parseHostControlMessage;

public class TcpHost extends AHost {

    private static final String TAG = "Host - lji1";
    private static final int UPDATE_INTERVAL = 200;
    private int port;
    public ServerSocket serverSocket;
    
    private ConcurrentLinkedQueue<String> incomingMessages = new ConcurrentLinkedQueue<>();
    
    public TcpHost(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(port);
        
        Gdx.app.log(TAG, "Creating socket for host instance with address: " +
                TcpNetworkUtils.getLocalHostAddr() + " on port: " + this.port);
        
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
                            new DataInputStream(socket.getInputStream()),
                            new DataOutputStream(socket.getOutputStream())
                    );
                    registry.put(socket.getInetAddress(), player);

                    // spawn a thread to listen on this socket
                    new Thread(() -> {
                        String msg = "";
                        while (true) {
                            try {
                                Gdx.app.log(TAG, "Host is listening for next client message from: "  + player.address);
                                msg = player.in.readUTF();
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
//        String msg = new String(packet.getData()).trim();
        
        if (!gameStarted) {
//             Game has not started
            HostControlMessage ctrlMsg = parseHostControlMessage(msg);
            if (ctrlMsg != null) {
                ctrlMsg.updateHost(this, addr, port);
            } else {
                Gdx.app.error(TAG, "Unrecognized message before start of game.");
            }
        } else {
            // Game has started
            Gdx.app.log(TAG, "Host received an update message from: " + addr.getHostAddress());
            incomingMessages.add(msg);
        }
        
        
    }
    
    public void broadcastToRegisteredPlayers(String msg) {
        registry.values().forEach((player) -> {
            System.out.println("Sending message: " + msg + " to player: " + player.address);
            sendMessage(msg, player);
        });
    }
    
    @Override
    public void sendMessage(String msg, Player player) {
        System.out.println(player);
        try {
            player.out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
