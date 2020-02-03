package com.week1.game.Networking.Messages.Game;

import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.GameState;
import com.week1.game.Model.Unit;
import com.week1.game.Networking.Messages.MessageType;

public class MoveMinionMessage extends GameMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.MOVE;

    private float x;
    private float y;
    private Array<Integer> minionIDs;

    public MoveMinionMessage(float x, float y, int playerID, Array<Unit> minions) {
        super(playerID, MESSAGE_TYPE);
        this.x = x;
        this.y = y;
        
        this.minionIDs = new Array<>();
        minions.forEach((minion) -> minionIDs.add(minion.ID));

    }
    @Override
    public boolean process(GameState inputState){
        
        minionIDs.forEach((id) -> {
            Unit minion = inputState.getMinionById(id);
            inputState.moveMinion(x, y, minion);
        });
        return true;
    }
    
    @Override
    public String toString() {
        return "MoveMinionMessage: " + minionIDs + " playerID: " + playerID;
    }
}
