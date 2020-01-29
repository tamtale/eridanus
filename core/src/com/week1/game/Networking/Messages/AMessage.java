package com.week1.game.Networking.Messages;

public abstract class AMessage {

    public int playerID; // Requires that every message has a playerID (necessary for network processing)
    MessageType messageTypeID;
    
    public AMessage(int playerID, MessageType messageTypeID) {
        this.playerID = playerID;
        this.messageTypeID = messageTypeID;
    }
    
}
