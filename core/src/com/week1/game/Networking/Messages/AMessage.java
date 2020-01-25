package com.week1.game.Networking.Messages;

public abstract class AMessage {

    int playerID; // Requires that every message has a playerID (neccessary for network processing)
    MessageType messageTypeID;
    
    public AMessage(int playerID, MessageType messageTypeID) {
        this.playerID = playerID;
        this.messageTypeID = messageTypeID;
    }
    
}
