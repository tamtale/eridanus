package com.week1.game.Networking.Messages;

import com.week1.game.Model.GameState;

public abstract class AMessage {

    int playerID; // Requires that every message has a playerID (neccessary for network processing)
    int messageTypeID;
    
    public AMessage(int playerID, int messageTypeID) {
        this.playerID = playerID;
        this.messageTypeID = messageTypeID;
    }
    
    abstract boolean process(GameState gameState);
}
