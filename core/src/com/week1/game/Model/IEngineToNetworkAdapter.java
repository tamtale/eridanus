package com.week1.game.Model;

import com.week1.game.Networking.Messages.AMessage;

public interface IEngineToNetworkAdapter {
    void sendMessage(AMessage msg);
}
