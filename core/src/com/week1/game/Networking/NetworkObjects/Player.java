package com.week1.game.Networking.NetworkObjects;

import com.badlogic.gdx.graphics.Color;
import com.week1.game.Model.Entities.UnitModel;
import com.week1.game.Model.PlayerInfo;
import com.week1.game.Pair;

import java.io.*;
import java.net.InetAddress;

public class Player {
    public int playerId;
    public InetAddress address;
    public int port;
    public boolean checkedIn;
    private PlayerInfo info = new PlayerInfo("defaultName");

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

    public void setPlayerInfo(PlayerInfo info) {
        this.info = info;
    }

    public String getFaction() {
        return info.getFaction();
    }

//    public String getFactionName() {
//        return info.getFactionName();
//    }

    public String getName() {
        return info.getPlayerName();
    }

    public PlayerInfo getInfo() {
        return this.info;
    }

    public void setFaction(String faction) {
        info.setFaction(faction);
    }
}
