package com.week1.game.Networking.Messages.Control;

import com.badlogic.gdx.Gdx;
import com.week1.game.Networking.NetworkObjects.AHost;
import com.week1.game.Networking.NetworkObjects.Udp.UdpHost;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Player;
import com.week1.game.TowerBuilder.BlockSpec;

import java.net.DatagramPacket;
import java.util.List;

public class JoinMessage extends HostControlMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.JOIN;
    private final static String TAG = "JoinMessage";
    
    private List<List<BlockSpec>> details;

    public JoinMessage(int playerID, List<List<BlockSpec>> details){
        super(playerID, MESSAGE_TYPE);
        this.details = details;
    }

    @Override 
    public void updateHost(AHost h, DatagramPacket p) {
        Gdx.app.log(TAG, "Host received a 'join' message from: " + p.getAddress().getHostAddress());

        h.towerDetails.add(details);
        h.registry.put(p.getAddress(), new Player(h.runningPlayerId++, p.getAddress(), p.getPort()));

        Gdx.app.log(TAG, "List of Players: ");
        h.registry.values().forEach((player) -> Gdx.app.log(TAG, "\t" + player.address + " : " + player.port));
    }

    @Override
    public String toString() {
        return "JoinMessage: " + playerID;
    }
}
