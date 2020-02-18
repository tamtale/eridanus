package com.week1.game.Networking.Messages.Game;

import com.week1.game.Model.GameState;
import com.week1.game.Networking.Messages.AMessage;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.InfoUtil;

public abstract class GameMessage extends AMessage {
    private int byteHash; // This is the byte hash of the game state used for synchronization checking

    public GameMessage(int playerID, MessageType messageTypeID, int byteHash) {
        super(playerID, messageTypeID);
        this.byteHash = byteHash;
    }
    
    public abstract boolean process(GameState gameState, InfoUtil util);

    public int getHashCode() {
        return byteHash;
    }
}
