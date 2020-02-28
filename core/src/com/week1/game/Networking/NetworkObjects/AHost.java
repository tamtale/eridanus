package com.week1.game.Networking.NetworkObjects;

import com.week1.game.TowerBuilder.BlockSpec;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AHost {
    public static final int DANGEROUS_HARDCODED_MESSAGE_SIZE = 4096;

    public Map<Integer, List<List<BlockSpec>>> towerDetails = new HashMap<>(); // first index is implicitly the player id
    public Map<InetAddress, Player> registry = new HashMap<>();

    public int runningPlayerId = 0;
    public boolean gameStarted = false;
    
    public abstract int getPort();
    public abstract void listenForClientMessages() throws IOException;
    public abstract void broadcastToRegisteredPlayers(String msg);
    public abstract void runUpdateLoop();
    public abstract void sendMessage(String msg, Player player);

}