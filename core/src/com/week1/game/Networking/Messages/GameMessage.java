package com.week1.game.Networking.Messages;

import com.week1.game.Model.GameState;

public abstract class GameMessage extends AMessage {
    public GameMessage(int playerID, MessageType messageTypeID) {
        super(playerID, messageTypeID);
    }
    
    public abstract boolean process(GameState gameState);
}
