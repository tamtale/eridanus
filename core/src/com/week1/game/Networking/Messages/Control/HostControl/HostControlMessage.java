package com.week1.game.Networking.Messages.Control.HostControl;

import com.week1.game.Networking.Messages.AMessage;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Host;

import java.net.InetAddress;

/**
 * Abstract type for messages that ask the HOST to change something, or broadcast a command to all clients
 */
public abstract class HostControlMessage extends AMessage {
    public HostControlMessage(int playerID, MessageType messageTypeID) {
        super(playerID, messageTypeID);
    }
    
    public abstract void updateHost(Host h, InetAddress addr, int port);
}
