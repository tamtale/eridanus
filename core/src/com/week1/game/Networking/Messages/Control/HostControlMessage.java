package com.week1.game.Networking.Messages.Control;

import com.week1.game.Networking.Messages.AMessage;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.AHost;

import java.net.InetAddress;

public abstract class HostControlMessage extends AMessage {
    public HostControlMessage(int playerID, MessageType messageTypeID) {
        super(playerID, messageTypeID);
    }
    
    public abstract void updateHost(AHost h, InetAddress addr, int port);
}
