package com.week1.game.Networking.Messages;

import com.week1.game.Model.GameState;

public class MoveMinionMessage extends AMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.MOVE;

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
