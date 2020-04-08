package com.week1.game.Networking.Messages.Control.ClientControl;

import com.week1.game.MenuScreens.CurrentScreenState;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Client;

public class GoToSplashscreenMessage extends ClientControlMessage {
    private final static MessageType type = MessageType.GOTOSPLASHSCREEN;

    private CurrentScreenState prevState;

    public GoToSplashscreenMessage(int playerID, CurrentScreenState state) {
        super(playerID, type);
        prevState = state;
    }

    @Override
    public void updateClient(Client c) {
        // TODO
    }
}
