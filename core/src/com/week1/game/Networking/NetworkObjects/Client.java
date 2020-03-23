package com.week1.game.Networking.NetworkObjects;

import com.badlogic.gdx.Gdx;
import com.week1.game.MenuScreens.ScreenManager;
import com.week1.game.Networking.INetworkClientToEngineAdapter;
import com.week1.game.Networking.Messages.Control.ClientControl.ClientControlMessage;
import com.week1.game.Networking.Messages.Control.HostControl.RequestGoToGameMessage;
import com.week1.game.Networking.Messages.Control.HostControl.RequestGoToLoadoutMessage;
import com.week1.game.Networking.Messages.Control.HostControl.RequestRestartMessage;
import com.week1.game.Networking.Messages.Control.HostControl.SubmitLoadoutMessage;
import com.week1.game.Networking.Messages.Game.GameMessage;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.TowerBuilder.BlockSpec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

/**
 * This is a player of the game. There is also a Client on the computer that has the host.
 */
public class Client {
    private static final String TAG = "Client - lji1";
 
    private Socket tcpSocket;
    private InetAddress hostAddress;
    private int hostPort;
    
    private DataInputStream in;
    private DataOutputStream out;
    
    private INetworkClientToEngineAdapter adapter;
    
    private int playerId = -1;
    private ScreenManager screenManager;
    
    public Client(String hostIpAddr, int hostPort, ScreenManager screenManager) throws IOException {
        this.hostAddress = InetAddress.getByName(hostIpAddr);
        this.hostPort = hostPort;
        this.screenManager = screenManager;
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
            Gdx.app.debug(TAG, "Sent message");
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
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void sendGoToGame(long mapSeed) {
        // the client doesn't know its player id until later, so just use -1
        this.sendStringMessage(MessageFormatter.packageMessage(new RequestGoToGameMessage(mapSeed, -1)));
    }

    public void sendLoadout(List<List<BlockSpec>> details) {
        // This is sent in the LoadoutScreen.
        sendStringMessage(MessageFormatter.packageMessage(new SubmitLoadoutMessage(playerId, details)));
    }

    public void sendGoToLoadout() {
        sendStringMessage(MessageFormatter.packageMessage(new RequestGoToLoadoutMessage(-1)));
    }

    public void sendRestartRequest() {
        sendStringMessage(MessageFormatter.packageMessage(new RequestRestartMessage(playerId)));
    }
    /**
     * This is needed because I don't want to add the adapter during creation because connection
     * should ha[pen before the game is created
     * @param networkClientToEngineAdapter
     */
    public void addAdapter(INetworkClientToEngineAdapter networkClientToEngineAdapter) {
        this.adapter = networkClientToEngineAdapter;
    }

    public ScreenManager getScreenManager() {
        return screenManager;
    }

}
