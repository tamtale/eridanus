package com.week1.game.Networking.Messages;

import com.week1.game.Model.GameState;
import com.week1.game.Networking.Client;

public class PlayerIdMessage extends ControlMessage {
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
