package com.week1.game.Networking.Messages.Control;

import com.week1.game.Networking.Client;
import com.week1.game.Networking.Messages.MessageType;

public class PlayerIdMessage extends ControlMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.PLAYERID;
    private final int numPlayers;

    public PlayerIdMessage(int playerID, int numPlayers){
        super(playerID, MESSAGE_TYPE);
        this.numPlayers = numPlayers;
    }

    @Override 
    public void updateClient(Client c) {
        c.setPlayerId(this.playerID);
        c.setNumberOfPlayer(this.numPlayers);
    }
    
    @Override
    public String toString() {
        return "PlayerIdMessage: " + playerID + " total players: " + numPlayers;
    }
}
