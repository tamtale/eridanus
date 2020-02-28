package com.week1.game.Networking.Messages.Control;

import com.week1.game.Networking.NetworkObjects.AClient;
import com.week1.game.Networking.Messages.AMessage;
import com.week1.game.Networking.Messages.MessageType;

public abstract class ClientControlMessage extends AMessage {
    public ClientControlMessage(int playerID, MessageType messageTypeID) {
        super(playerID, messageTypeID);
    }
    
    public abstract void updateClient(AClient c);
}
