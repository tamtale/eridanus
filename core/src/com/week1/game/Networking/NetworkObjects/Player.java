package com.week1.game.Networking.NetworkObjects;

import java.io.*;
import java.net.InetAddress;

public class Player {
    public int playerId;
    public InetAddress address;
    public int port;
    public boolean checkedIn;

    public BufferedReader in;
    public BufferedWriter out;
    
    public Player(int playerId, InetAddress address, int port, InputStream in, OutputStream out) {
        this.playerId = playerId;
        this.address = address;
        this.port = port;
        this.checkedIn = false;
        this.in = new BufferedReader(new InputStreamReader(in));
        this.out = new BufferedWriter(new OutputStreamWriter(out));
    }
}
