package com.week1.game.Networking.Messages.Control.ClientControl;

import com.badlogic.gdx.Gdx;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Client;

/**
 * This is the message that is sent to command clients to go to the gameScreen to start playing the game
 */
public class GoToGameMessage extends ClientControlMessage {
    private final static MessageType type = MessageType.GOTOGAME;

    public GoToGameMessage(int playerID) {
        super(playerID, type);
    }

    @Override
    public void updateClient(Client c) {
        Gdx.app.log("pjb3 - GoToGameMessage", "running....");
        c.getScreenManager().setScreen(c.getScreenManager().getGameScreen());
    }
}
