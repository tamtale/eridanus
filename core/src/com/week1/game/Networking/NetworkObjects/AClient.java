package com.week1.game.Networking.NetworkObjects;

public abstract class AClient {
    public abstract String getHostAddr();
    public abstract void sendStringMessage(String msg);
    public abstract void awaitUpdates();
    public abstract void setPlayerId(int playerId);
    public abstract int getPlayerId();
    public abstract void sendStartMessage();
}