package com.week1.game.Networking.Messages.Control;

import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.AHost;
import com.week1.game.TowerBuilder.BlockSpec;

import java.net.InetAddress;
import java.util.List;

public class SendChosenTowersMessage extends HostControlMessage {

    private final static MessageType MESSAGE_TYPE = MessageType.TCPJOIN;
    private final static String TAG = "SendChosenTowersMessage";

    private List<List<BlockSpec>> details;

    public SendChosenTowersMessage(int playerID, List<List<BlockSpec>> details){
        super(playerID, MESSAGE_TYPE);
        this.details = details;
    }

    @Override
    public void updateHost(AHost h, InetAddress addr, int port) {
        h.towerDetails.put(playerID, details);
    }

    @Override
    public String toString() {
        return "SendChosenTowersMessage.";
    }
}
