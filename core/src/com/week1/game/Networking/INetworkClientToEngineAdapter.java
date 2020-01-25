package com.week1.game.Networking;

import com.week1.game.Networking.Messages.GameMessage;

import java.util.List;

public interface INetworkClientToEngineAdapter {
    void deliverUpdate(List<? extends GameMessage> messages);
}
