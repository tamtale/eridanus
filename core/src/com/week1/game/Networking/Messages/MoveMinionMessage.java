package com.week1.game.Networking.Messages;

import com.badlogic.gdx.graphics.Texture;
import com.week1.game.Model.DrawFunction;
import com.week1.game.Model.GameState;
import com.week1.game.Model.Unit;
import com.week1.game.Networking.MessageTypes;

public class MoveMinionMessage extends AMessage {
    private final static int MESSAGE_TYPE = MessageTypes.MOVE.ordinal();

    private int exampleField; //TODO: remove once real fields are added

    private float x;
    private float y;
    private int unitType;
    private int minionID;

    public MoveMinionMessage(float x, float y, int unitType, int playerID, int minionID){
        super(playerID, MESSAGE_TYPE);
        this.x = x;
        this.y = y;
        this.unitType = unitType;
        this.minionID = minionID;
    }

    @Override
    public boolean process(GameState inputState){
        
        // TODO: implement

        inputState.moveMinion(x, y, minionID);
        return true;
    }
    
    @Override
    public String toString() {
        return "MoveMinionMessage: " + exampleField + ", " + playerID;
    }
}
