package com.week1.game.Networking;

import com.week1.game.Networking.Messages.AMessage;

import java.util.List;

public interface INetworkClientToEngineAdapter {
    void deliverUpdate(List<? extends AMessage> messages);
}
