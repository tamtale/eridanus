package com.week1.game.Networking.Messages.Control;

import com.badlogic.gdx.Gdx;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.AHost;
import com.week1.game.Networking.NetworkObjects.Player;
import com.week1.game.TowerBuilder.BlockSpec;

import java.net.InetAddress;
import java.util.List;

public class TcpJoinMessage extends HostControlMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.TCPJOIN;
    private final static String TAG = "JoinMessage";
    
    private List<List<BlockSpec>> details;

    public TcpJoinMessage(int playerID, List<List<BlockSpec>> details){
        super(playerID, MESSAGE_TYPE);
        this.details = details;
    }

    @Override 
    public void updateHost(AHost h, InetAddress addr, int port) {
        Gdx.app.log(TAG, "Host received a 'tcp join' message from: " + addr.getHostAddress());

        h.towerDetails.add(details);
        // player has already been added to the registry on serverSocket.accept()
//        h.registry.put(addr, new Player(h.runningPlayerId++, addr, port));

        Gdx.app.log(TAG, "List of Players: ");
        h.registry.values().forEach((player) -> Gdx.app.log(TAG, "\t" + player.address + " : " + player.port));
    }

    @Override
    public String toString() {
        return "JoinMessage: " + playerID;
    }
}
