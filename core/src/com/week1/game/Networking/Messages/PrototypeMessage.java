package com.week1.game.Networking.Messages;

import com.week1.game.Model.GameState;

public class PrototypeMessage extends AMessage {
    public PrototypeMessage(int playerID, int messageTypeID) {
        super(playerID, messageTypeID);
    }

    @Override
    boolean process(GameState gameState) {
        return false;
    }
    
    
    
    @Override
    public String toString() {
        return "Prototype message: playerID: " + this.playerID + " messageTypeID: " + this.messageTypeID;
    }
}
