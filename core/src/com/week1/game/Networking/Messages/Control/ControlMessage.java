package com.week1.game.Networking.Messages.Control;

import com.week1.game.Networking.Client;
import com.week1.game.Networking.Messages.AMessage;
import com.week1.game.Networking.Messages.MessageType;

public abstract class ControlMessage extends AMessage {
    public ControlMessage (int playerID, MessageType messageTypeID) {
        super(playerID, messageTypeID);
    }
    
    public abstract void updateClient(Client c);
}
