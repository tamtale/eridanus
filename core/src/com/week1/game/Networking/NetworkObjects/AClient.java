package com.week1.game.Networking.NetworkObjects;

import com.week1.game.MenuScreens.ScreenManager;
import com.week1.game.Networking.INetworkClientToEngineAdapter;

public abstract class AClient {
    public abstract String getHostAddr();
    public abstract void sendStringMessage(String msg);
    public abstract void awaitUpdates();
    public abstract void setPlayerId(int playerId);
    public abstract int getPlayerId();
    public abstract void sendStartMessage();
    public abstract ScreenManager getScreenManager();
    public abstract void addAdapter(INetworkClientToEngineAdapter iNetworkClientToEngineAdapter);
}