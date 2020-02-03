package com.week1.game.Networking.Messages.Game;

import com.week1.game.Model.GameState;
import com.week1.game.Networking.Messages.AMessage;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.InfoUtil;

public abstract class GameMessage extends AMessage {
    public GameMessage(int playerID, MessageType messageTypeID) {
        super(playerID, messageTypeID);
    }
    
    public abstract boolean process(GameState gameState, InfoUtil util);
}
