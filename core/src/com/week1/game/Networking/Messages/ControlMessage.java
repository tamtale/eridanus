package com.week1.game.Networking.Messages;

import com.week1.game.Networking.Client;

public abstract class ControlMessage extends AMessage {
    public ControlMessage (int playerID, MessageType messageTypeID) {
        super(playerID, messageTypeID);
    }
    
    public abstract void updateClient(Client c);
}
