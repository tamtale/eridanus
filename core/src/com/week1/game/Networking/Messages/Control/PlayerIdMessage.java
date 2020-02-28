package com.week1.game.Networking.Messages.Control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.AClient;

public class PlayerIdMessage extends ClientControlMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.PLAYERID;

    public PlayerIdMessage(int playerID){
        super(playerID, MESSAGE_TYPE);
    }

    @Override 
    public void updateClient(AClient c) {
        c.setPlayerId(this.playerID);
        Screen gameScreen = c.getGameScreen();
        Gdx.app.log("pjb3 - PlayerIDMessage.", " About to set the gamescreen to be: " + gameScreen);
        c.goToGameScreen(gameScreen);
    }
    
    @Override
    public String toString() {
        return "PlayerIdMessage: " + playerID;
    }
}
