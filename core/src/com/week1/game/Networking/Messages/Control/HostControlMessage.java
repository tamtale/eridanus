package com.week1.game.Networking.Messages.Control;

import com.week1.game.Networking.NetworkObjects.AHost;
import com.week1.game.Networking.NetworkObjects.Udp.UdpHost;
import com.week1.game.Networking.Messages.AMessage;
import com.week1.game.Networking.Messages.MessageType;

import java.net.DatagramPacket;
import java.net.InetAddress;

public abstract class HostControlMessage extends AMessage {
    public HostControlMessage(int playerID, MessageType messageTypeID) {
        super(playerID, messageTypeID);
    }
    
    public abstract void updateHost(AHost h, InetAddress addr, int port);
}
