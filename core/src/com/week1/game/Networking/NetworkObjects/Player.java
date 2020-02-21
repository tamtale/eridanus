package com.week1.game.Networking.NetworkObjects;

import java.net.InetAddress;

public class Player {
    public int playerId;
    public InetAddress address;
    public int port;
    public boolean checkedIn;
    
    public Player(int playerId, InetAddress address, int port) {
        this.playerId = playerId;
        this.address = address;
        this.port = port;
        checkedIn = false;
    }
}
