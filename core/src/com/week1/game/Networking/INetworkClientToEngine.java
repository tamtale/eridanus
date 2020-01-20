package com.week1.game.Networking;

import com.week1.game.Networking.Messages.IMessage;

import java.util.List;

public interface INetworkClientToEngine {
    void deliverUpdate(List<IMessage> messages);
}
