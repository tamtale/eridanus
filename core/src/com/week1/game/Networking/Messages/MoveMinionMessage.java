package com.week1.game.Networking.Messages;

import com.week1.game.Model.GameState;
import com.week1.game.Model.Unit;
import com.week1.game.Networking.MessageTypes;

public class MoveMinionMessage extends AMessage {
    private final static int MESSAGE_TYPE = MessageTypes.MOVE.ordinal();

    private int exampleField; //TODO: remove once real fields are added

    public MoveMinionMessage(float x, float y, int unitType, int playerID){
        super(playerID, MESSAGE_TYPE);
    }

    @Override
    public boolean process(GameState inputState){
        
        // TODO: implement
        
        return true;
    }
    
    @Override
    public String toString() {
        return "MoveMinionMessage: " + exampleField + ", " + playerID;
    }
}
