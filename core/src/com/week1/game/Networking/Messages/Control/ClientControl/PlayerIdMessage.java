package com.week1.game.Networking.Messages.Control.ClientControl;

import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Client;

/**
 * This message is sent to a player right after they connect to the Host
 */
public class PlayerIdMessage extends ClientControlMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.PLAYERID;

    public PlayerIdMessage(int playerID){
        super(playerID, MESSAGE_TYPE);
    }

    @Override 
    public void updateClient(Client c) {
        c.setPlayerId(this.playerID);
    }
    
    @Override
    public String toString() {
        return "PlayerIdMessage: " + playerID;
    }
}
