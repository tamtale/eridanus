package com.week1.game.Model;

import com.week1.game.Networking.Messages.AMessage;

public interface IClickOracleToNetworkAdapter {
    void sendMessage(AMessage msg);
    int getPlayerId();
}
