package com.week1.game.Networking.Messages.Control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.TowerDetails;
import com.week1.game.Networking.Host;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.Player;

import java.net.DatagramPacket;

public class JoinMessage extends HostControlMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.JOIN;
    private final static String TAG = "JoinMessage";
    
    private Array<TowerDetails> details;

    public JoinMessage(int playerID, Array<TowerDetails> details){
        super(playerID, MESSAGE_TYPE);
        this.details = details;
    }

    @Override 
    public void updateHost(Host h, DatagramPacket p) {
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
