package com.week1.game.Networking.Messages.Control.ClientControl;

import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Client;

public class GoToSplashscreenMessage extends ClientControlMessage {
    private final static MessageType type = MessageType.GOTOSPLASHSCREEN;

    public GoToSplashscreenMessage(int playerID) {
        super(playerID, type);
    }

    @Override
    public void updateClient(Client c) {
        c.disconnect();
    }
}
