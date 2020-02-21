package com.week1.game.Networking.Messages.Control;

import com.week1.game.Networking.Client;
import com.week1.game.Networking.Host;
import com.week1.game.Networking.Messages.AMessage;
import com.week1.game.Networking.Messages.MessageType;

import java.net.DatagramPacket;

public abstract class HostControlMessage extends AMessage {
    public HostControlMessage(int playerID, MessageType messageTypeID) {
        super(playerID, messageTypeID);
    }
    
    public abstract void updateHost(Host h, DatagramPacket p);
}
