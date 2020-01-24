package com.week1.game.Networking.Messages;

import com.week1.game.Model.GameState;

public abstract class AMessage {

    int playerID; // Requires that every message has a playerID (neccessary for network processing)
    MessageType messageTypeID;
    
    public AMessage(int playerID, MessageType messageTypeID) {
        this.playerID = playerID;
        this.messageTypeID = messageTypeID;
    }
    
    public abstract boolean process(GameState gameState);
}
