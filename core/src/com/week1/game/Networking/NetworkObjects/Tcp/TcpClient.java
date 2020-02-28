package com.week1.game.Networking.NetworkObjects.Tcp;

import com.badlogic.gdx.Gdx;
import com.week1.game.GameControllerSetScreenAdapter;
import com.week1.game.GameScreen;
import com.week1.game.LoadoutPage.LoadoutScreen;
import com.week1.game.Networking.INetworkClientToEngineAdapter;
import com.week1.game.Networking.Messages.Control.*;
import com.week1.game.Networking.Messages.Game.GameMessage;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.NetworkObjects.AClient;
import com.week1.game.TowerBuilder.BlockSpec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class TcpClient extends AClient {
    private static final String TAG = "Client - lji1";
 
    private Socket tcpSocket;
    private InetAddress hostAddress;
    private int hostPort;
    
    private DataInputStream in;
    private DataOutputStream out;
    
    private INetworkClientToEngineAdapter adapter;
    
    private int playerId = -1;
    private boolean isHostingClient;
    private boolean playerIDReady = false;
    private GameControllerSetScreenAdapter game; // This is needed so that the network can change the stage of the game back to the TowerLoadout
    
    
    public TcpClient(String hostIpAddr, int hostPort, boolean isHostingClient, GameControllerSetScreenAdapter game) throws IOException {
        this.hostAddress = InetAddress.getByName(hostIpAddr);
        this.hostPort = hostPort;
        this.isHostingClient = isHostingClient;
        this.game = game;
        this.tcpSocket = new Socket(hostAddress, hostPort);
        this.in = new DataInputStream(tcpSocket.getInputStream());
        this.out = new DataOutputStream(tcpSocket.getOutputStream());
        Gdx.app.log(TAG, "Created socket for client instance on port: " + tcpSocket.getLocalPort());
        
        awaitUpdates();
    }

    public String getHostAddr() {
        return hostAddress.toString() + ":" + hostPort;
    }
    
    public void sendStringMessage(String msg) {
        Gdx.app.log(TAG, "About to send message: " + msg + " to: " + hostAddress + ":" + this.hostPort);
        try {
            this.out.writeUTF(msg);
            Gdx.app.log(TAG, "Sent message");
        } catch (IOException e) {
            Gdx.app.error(TAG, "Failed to send message: " + msg);
        }
        
    }
    
    
    public void awaitUpdates() {
        
        new Thread(() -> {
            while (true) {
//                byte[] buf = new byte[TcpHost.DANGEROUS_HARDCODED_MESSAGE_SIZE]; // TODO: size this according to message length
//                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                try {
                    // blocks until a packet is received
//                    udpSocket.receive(packet);
//                    String messages = new String(packet.getData()).trim();
                    String messages = this.in.readUTF();
                    
                    
                    Gdx.app.log(TAG, "About to try parsing message: " + messages);
                    // try parsing as a control message first
                    ClientControlMessage controlMsg = MessageFormatter.parseClientControlMessage(messages);
                    if (controlMsg != null) {
                        Gdx.app.log(TAG, "Received control message: " + controlMsg);
                        controlMsg.updateClient(this);
                        continue; // don't need to try parsing as game messages if already successfully parsed as control message
                    }
                    
                    Gdx.app.debug(TAG, "Received update: " + messages);
                    List<GameMessage> msgList = MessageFormatter.parseMessages(messages);
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
        if (adapter == null) {
            // This means the adapter has not been set yet. Set a variable so it is hit when the adapter comes in
            // and then recalls this.
            // TODO probably sketchy to do this. Need a better idea for the future.
            playerIDReady = true;
        } else {
            adapter.setPlayerId(playerId);
        }
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void sendStartMessage() {
        System.out.println("Trying to send start message.");
        // the client doesn't know its player id until later, so just use -1
        this.sendStringMessage(MessageFormatter.packageMessage(new StartMessage(-1)));
    }
    
    public void sendJoinMessage() {
        Gdx.app.log(TAG, "Sending join message.");
        // the client doesn't know its player id until later, so just use -1
        sendStringMessage(MessageFormatter.packageMessage(new TcpJoinMessage(-1)));
    }

    public void sendTowersMessage(List<List<BlockSpec>> details) {
        sendStringMessage(MessageFormatter.packageMessage(new SendChosenTowersMessage(-1, details)));
    }

    /**
     * This is needed because I don't want to add the adapter during creation because connection
     * should ha[pen before the game is created
     * @param networkClientToEngineAdapter
     */
    public void addAdapter(INetworkClientToEngineAdapter networkClientToEngineAdapter) {
        this.adapter = networkClientToEngineAdapter;
        if (playerIDReady) {
            // This means the message already came in and now the adapter is here it should be added
            setPlayerId(playerId);
            playerIDReady = false;
        }
    }

    @Override
    public void sendGoToLoadout() {
        sendStringMessage(MessageFormatter.packageMessage(new RequestGoToLoadoutMessage(-1)));
    }

    public void createNewLoadoutScreen() {
        // Set the Screen to the Loadout screen when the render thread is ready
        Gdx.app.postRunnable(() -> game.setScreen(new LoadoutScreen(game, this, isHostingClient)));
    }

    public void goToGameScreen(GameScreen newGame) {
        Gdx.app.postRunnable(() -> game.setScreen(newGame));

    }
}
