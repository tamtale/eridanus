package com.week1.game.Networking.NetworkObjects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;

public class Player {
    public int playerId;
    public InetAddress address;
    public int port;
    public boolean checkedIn;

    // Only used by TCP
    public DataInputStream in;
    public DataOutputStream out;
    
    public Player(int playerId, InetAddress address, int port, DataInputStream in, DataOutputStream out) {
        this.playerId = playerId;
        this.address = address;
        this.port = port;
        this.checkedIn = false;
        this.in = in;
        this.out = out;
    }
    
    public Player(int playerId, InetAddress address, int port) {
        this.playerId = playerId;
        this.address = address;
        this.port = port;
        this.checkedIn = false;
    }
}
