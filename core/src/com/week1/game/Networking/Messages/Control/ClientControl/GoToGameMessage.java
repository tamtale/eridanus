package com.week1.game.Networking.Messages.Control.ClientControl;

import com.badlogic.gdx.Gdx;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.AClient;

public class GoToGameMessage extends ClientControlMessage {
    private final static MessageType type = MessageType.GOTOGAME;

    public GoToGameMessage(int playerID) {
        super(playerID, type);
    }

    @Override
    public void updateClient(AClient c) {
        Gdx.app.log("pjb3 - GoToGameMessage", "running....");
        c.getScreenManager().setScreen(c.getScreenManager().getGameScreen());
    }
}
