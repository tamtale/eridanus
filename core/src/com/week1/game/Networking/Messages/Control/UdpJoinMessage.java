package com.week1.game.Networking.Messages.Control;

import com.badlogic.gdx.Gdx;
import com.week1.game.Networking.NetworkObjects.AHost;
import com.week1.game.Networking.NetworkObjects.Udp.UdpHost;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Player;
import com.week1.game.TowerBuilder.BlockSpec;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.List;

public class UdpJoinMessage extends HostControlMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.UDPJOIN;
    private final static String TAG = "JoinMessage";
    
    private List<List<BlockSpec>> details;

    public UdpJoinMessage(int playerID, List<List<BlockSpec>> details){
        super(playerID, MESSAGE_TYPE);
        this.details = details;
    }

    @Override 
    public void updateHost(AHost h, InetAddress addr, int port) {
        Gdx.app.log(TAG, "Host received a 'join' message from: " + addr.getHostAddress());

        h.towerDetails.add(details);
        h.registry.put(addr, new Player(h.runningPlayerId++, addr, port));

        Gdx.app.log(TAG, "List of Players: ");
        h.registry.values().forEach((player) -> Gdx.app.log(TAG, "\t" + player.address + " : " + player.port));
    }

    @Override
    public String toString() {
        return "JoinMessage: " + playerID;
    }
}
