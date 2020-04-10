package com.week1.game.Networking.NetworkObjects;

import com.badlogic.gdx.Gdx;
import com.week1.game.MenuScreens.ScreenManager;
import com.week1.game.Model.PlayerInfo;
import com.week1.game.Model.TowerLite;
import com.week1.game.Networking.INetworkClientToEngineAdapter;
import com.week1.game.Networking.Messages.Control.ClientControl.ClientControlMessage;
import com.week1.game.Networking.Messages.Control.HostControl.*;
import com.week1.game.Networking.Messages.Game.GameMessage;
import com.week1.game.Networking.Messages.MessageFormatter;

import java.io.*;
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
    
    private BufferedReader in;
    private BufferedWriter out;
    
    private INetworkClientToEngineAdapter adapter;
    private Thread updateThread;
    
    private int playerId = -1;
    private List<PlayerInfo> infoList; // info of all the players in the game.
    private ScreenManager screenManager;
    
    public Client(String hostIpAddr, int hostPort, ScreenManager screenManager) throws IOException {
        this.hostAddress = InetAddress.getByName(hostIpAddr);
        this.hostPort = hostPort;
        this.screenManager = screenManager;
        this.tcpSocket = new Socket(hostAddress, hostPort);
        this.in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(tcpSocket.getOutputStream()));
        Gdx.app.log(TAG, "Created socket for client instance on port: " + tcpSocket.getLocalPort());
        
        awaitUpdates();
    }

    public String getHostAddr() {
        return hostAddress.toString() + ":" + hostPort;
    }
    
    public void sendStringMessage(String msg) {
        Gdx.app.debug(TAG, "About to send message: " + msg + " to: " + hostAddress + ":" + this.hostPort);
        try {
            this.out.write(msg + "\n");
            this.out.flush();
            Gdx.app.debug(TAG, "Sent message");
        } catch (IOException e) {
            Gdx.app.error(TAG, "Failed to send message: " + msg);
        }
        
    }
    
    public void awaitUpdates() {
        updateThread = new Thread(() -> {
            while (true) {
                try {
                    String messages = this.in.readLine();
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
                    if(updateThread.isInterrupted()) {
                        return;
                    }
                    e.printStackTrace();
                    Gdx.app.error(TAG, "Failed to receive update messages.");
                }
            }

        });
        updateThread.start();
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

    public void sendLoadout(List<TowerLite> details) {
        // This is sent in the LoadoutScreen.
        sendStringMessage(MessageFormatter.packageMessage(new SubmitLoadoutMessage(playerId, details)));
    }

    public void sendGoToLoadout() {
        sendStringMessage(MessageFormatter.packageMessage(new RequestGoToLoadoutMessage(-1)));
    }

    public void sendDisconnectRequest() {
        sendStringMessage(MessageFormatter.packageMessage(new RequestGoToSpashscreen(-1)));
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

    public void sendPlayerInfo(PlayerInfo info) {
        sendStringMessage(MessageFormatter.packageMessage(new SubmitPlayerInfo(info)));
    }

    public List<PlayerInfo> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<PlayerInfo> infoList) {
        this.infoList = infoList;
    }

    public void disconnect() {
        updateThread.interrupt();
        try {
            tcpSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        screenManager.goToSplashScreen();
    }
}
