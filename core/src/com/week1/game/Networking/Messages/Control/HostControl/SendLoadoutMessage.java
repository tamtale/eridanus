package com.week1.game.Networking.Messages.Control.HostControl;

import com.badlogic.gdx.Gdx;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Host;
import com.week1.game.TowerBuilder.BlockSpec;

import java.net.InetAddress;
import java.util.List;

public class SendLoadoutMessage extends HostControlMessage {

    private final static MessageType MESSAGE_TYPE = MessageType.SENDLOADOUT;
    private final static String TAG = "SendChosenTowersMessage";

    private List<List<BlockSpec>> details;

    public SendLoadoutMessage(int playerID, List<List<BlockSpec>> details){
        super(playerID, MESSAGE_TYPE);
        this.details = details;
    }

    @Override
    public void updateHost(Host h, InetAddress addr, int port) {
        Gdx.app.log("pjb3 -  SendLoadoutMessage", "Adding the tower details for the player " + playerID);
        h.towerDetails.put(playerID, details);
    }

    @Override
    public String toString() {
        return "SendChosenTowersMessage.";
    }
}
