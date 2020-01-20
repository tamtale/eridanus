package com.week1.game.Networking;

import java.util.List;

public interface INetworkClientToEngine {
    
    void deliverUpdate(List<IMessage> messages);
}
