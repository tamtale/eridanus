package com.week1.game.Networking.NetworkObjects;

import com.badlogic.gdx.Gdx;
import com.week1.game.Model.PlayerInfo;
import com.week1.game.Model.TowerLite;
import com.week1.game.Networking.Messages.Control.ClientControl.JoinedPlayersMessage;
import com.week1.game.Networking.Messages.Control.ClientControl.PlayerIdMessage;
import com.week1.game.Networking.Messages.Control.HostControl.HostControlMessage;
import com.week1.game.Networking.Messages.Game.CheckSyncMessage;
import com.week1.game.Networking.Messages.Game.GameMessage;
import com.week1.game.Networking.Messages.Game.SyncIssueMessage;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.Messages.Update;
import com.week1.game.Pair;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.week1.game.Networking.Messages.MessageFormatter.parseHostControlMessage;
import static com.week1.game.Networking.Messages.MessageType.SYNCERR;


/**
 * This is the Host that receives and broadcasts messages to all of the clients.
 */
public class Host {

    private static final String TAG = "Host - lji1/pjb3";
    private static final int UPDATE_INTERVAL = 200;
    private int port;
    public ServerSocket serverSocket;
    private int nextPlayerId = 0;
    private boolean magicBoolean = false;

    List<String> joinedPlayers = Collections.synchronizedList(new ArrayList<>());
    public Map<Integer, List<TowerLite>> towerDetails = new HashMap<>(); // first index is implicitly the player id
    public Map<InetAddress, Player> registry = new HashMap<>();

    public int runningPlayerId = 0;
    public boolean gameStarted = false;
    private boolean loopStarted = false;
    
    private ConcurrentLinkedQueue<String> incomingMessages = new ConcurrentLinkedQueue<>();
    Thread joiningPlayersThread, updateLoopThread, listeningThread;

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
        joiningPlayersThread = new Thread(() -> {
            while (true) {
                try {
                    if (joiningPlayersThread.isInterrupted()) {
                        return;
                    }
                    Gdx.app.log("Host - pjb3", "waiting for a new client to join...");
                    Socket socket = this.serverSocket.accept();
                    Player player = new Player(
                            this.runningPlayerId++,
                            socket.getInetAddress(),
                            socket.getPort(),
                            socket.getInputStream(),
                            socket.getOutputStream()
                    );
                    Gdx.app.log("Host -pjb3", "NEW CLIENT yaya. Set is:" + registry.keySet()+ " and adding new:" + socket.getInetAddress());
                    registry.put(socket.getInetAddress(), player);

                    // Tell them their playerID
                    String playerIdMessage = MessageFormatter.packageMessage(new PlayerIdMessage(player.playerId));
                    sendMessage(playerIdMessage, player);

                    // spawn a thread to listen on this socket
                    listeningThread = new Thread(() -> {
                        String msg = "";
                        while (true) {
                            try {
                                if (listeningThread.isInterrupted()) {
                                    return;
                                }
                                Gdx.app.debug(TAG, "Host is listening for next client message from: "  + player.address);
                                msg = player.in.readLine();
                                processMessage(msg, player.address, player.port);


                            } catch (IOException e) {
                                if (listeningThread.isInterrupted()) {
                                    return;
                                }
                                e.printStackTrace();
                            }
                        }

                    });
                    listeningThread.start();
                } catch (IOException e) {
                    if (joiningPlayersThread.isInterrupted()) {
                        return;
                    }
                    e.printStackTrace();
                }
            }
        });
        joiningPlayersThread.start();
    }

    public void runUpdateLoop() {

        if (loopStarted) {
            return;
        }
        loopStarted = true;
        // spawn a new thread to broadcast updates to the registered clients
        Gdx.app.log(TAG, "Host is about to begin running update loop.");
        updateLoopThread = new Thread(() -> {
            while (true) {
                if (updateLoopThread.isInterrupted() || magicBoolean) {
                    Gdx.app.log("Host pjb3" ,"updateloop STOPPING");
                    return;
                } else {
                    Gdx.app.log("Host pjb3" ,"I AINT STOPPING");
                }
                List<String> outgoingMessages = new ArrayList<>();
                while (!incomingMessages.isEmpty()) { // TODO: dangerous, if many messages coming all at once
                    outgoingMessages.add(incomingMessages.poll());
                }

                // Verify game state on any messages sent this game turn
                int prevHash = 0, prevTurn = 0; // This will be the hash that everything is compared to
                boolean didFail = false;
                boolean didCheck = false;
                List<Integer> hashes = new ArrayList<>();
                for (String outgoingMessage : outgoingMessages) {
                    GameMessage msg = MessageFormatter.parseMessage(outgoingMessage);
                    if (msg instanceof CheckSyncMessage) {
                        didCheck = true;
                        int hash = msg.getHashCode();
                        hashes.add(hash);
                        if (prevHash == 0) {
                            // if it has not been set, set the standard to this one.
                            prevHash = hash;
                            prevTurn = ((CheckSyncMessage) msg).getTurn();
                        } else {
                            if (prevHash != hash && prevTurn == ((CheckSyncMessage) msg).getTurn()) {
                                Gdx.app.log("pjb3 - Host", "ERROR: The hashes do not match for two messages!!!!! Yikes.");
                                // Create a SyncIssue message to send to all clients so they can know there is an issue
                                didFail = true;
                            }
                        }
                    }
                }
                if (didFail) {
                    outgoingMessages.add(0, MessageFormatter.packageMessage(new SyncIssueMessage(-1, SYNCERR, prevHash, hashes)));
                } else if (didCheck) {
                    Gdx.app.log("pjb3 - Host", "Nice. The hashes match up.");
                }

                Gdx.app.debug(TAG, "Host is about to broadcast update message to registered clients.");

                broadcastToRegisteredPlayers(MessageFormatter.packageMessage(new Update(outgoingMessages)));

                // Take a break before the next update
                try {
                    Thread.sleep(UPDATE_INTERVAL);
                } catch (InterruptedException e) {
                    if (updateLoopThread.isInterrupted() || magicBoolean) {
                        return;
                    }
                    e.printStackTrace();
                }

            }
        });
        updateLoopThread.start();
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
            if (updateLoopThread != null && updateLoopThread.isInterrupted()) {
                Gdx.app.log("Host pjb3", "Stopping sending a message. disconnecting....");
                return;
            }
            player.out.write(msg + "\n");
            player.out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNextPlayerId() {
        return nextPlayerId;
    }

    public void setPlayerInfo(InetAddress address, PlayerInfo info) {
        this.registry.get(address).setPlayerInfo(info);
        int id = this.registry.get(address).playerId;
        // Tell everyone that someone has joined the game

        joinedPlayers.clear();
        registry.forEach((addr, plyr) -> joinedPlayers.add(plyr.playerId + ": " + plyr.getName()));
        java.util.Collections.sort(joinedPlayers);
        Gdx.app.debug("pjb3", "broadcasting" + joinedPlayers);
        broadcastToRegisteredPlayers(MessageFormatter.packageMessage(new JoinedPlayersMessage(-1, joinedPlayers)));
    }

    public List<PlayerInfo> getPlayerInfoList() {
        List<Pair<Integer, PlayerInfo>> nameList = new ArrayList<>();
        List<PlayerInfo> infoList = new ArrayList<>();
        registry.forEach((addr, plyr) -> nameList.add(new Pair<>(plyr.playerId, plyr.getInfo())));
        nameList.sort(Comparator.comparingInt(pair -> pair.key));
        nameList.forEach( pair -> infoList.add(pair.value));
        return infoList;
    }

    public void shutdown() {
        Gdx.app.log("pjb3", "Shutting down Host");
        magicBoolean = true; // This is to strong-handedly deal with the sleeping thread not realizing it was interrupted for some dumb reason
        if (joiningPlayersThread != null) {
            Gdx.app.log("pjb3", "Shutting down joiningplayersthread");
            joiningPlayersThread.interrupt();
        }
        if (listeningThread != null) {
            Gdx.app.log("pjb3", "Shutting down listeningThreas");
            listeningThread.interrupt();
        }
        if (updateLoopThread != null) {
            Gdx.app.log("pjb3", "Shutting down updateLoopThread");
            updateLoopThread.interrupt();
        }

        try {
            Gdx.app.log("pjb3", "Shutting down serversocket");
            serverSocket.close();
            registry.forEach((addr, plyr) -> {
                try {
                    plyr.in.close();
                    plyr.out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
