package com.week1.game.Networking.Messages.Control.ClientControl;

import com.badlogic.gdx.Gdx;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Client;

public class RestartMessage extends ClientControlMessage {
    private final static MessageType type = MessageType.RESTART;

    public RestartMessage(int playerID) {
        super(playerID, type);
    }

    @Override
    public void updateClient(Client c) {
        Gdx.app.debug("pjb3 - RestartGame", "");
        c.getScreenManager().restartGame(c);
    }
}
