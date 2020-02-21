package com.week1.game.Networking.Messages.Control;

import com.week1.game.Networking.NetworkObjects.AClient;
import com.week1.game.Networking.NetworkObjects.Udp.UdpClient;
import com.week1.game.Networking.Messages.MessageType;

public class PlayerIdMessage extends ClientControlMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.PLAYERID;

    public PlayerIdMessage(int playerID){
        super(playerID, MESSAGE_TYPE);
    }

    @Override 
    public void updateClient(AClient c) {
        c.setPlayerId(this.playerID);
    }
    
    @Override
    public String toString() {
        return "PlayerIdMessage: " + playerID;
    }
}
