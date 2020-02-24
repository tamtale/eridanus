package com.week1.game.Networking.Messages.Game;

import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.GameEngine;
import com.week1.game.Model.GameState;
import com.week1.game.Model.Entities.Unit;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.InfoUtil;

public class MoveMinionMessage extends GameMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.MOVE;

    private float x;
    private float y;
    private Array<Integer> minionIDs;

    public MoveMinionMessage(float x, float y, int playerID, Array<Unit> minions, int intHash) {
        super(playerID, MESSAGE_TYPE, intHash);
        this.x = x;
        this.y = y;

        this.minionIDs = new Array<>();
        minions.forEach((minion) -> minionIDs.add(minion.ID));
    }


    @Override
    public boolean process(GameEngine engine, GameState inputState, InfoUtil util){
        float centerX = 0;//x - inputState.getMinionById(minionIDs.get(0)).x;
        float centerY = 0;//y - inputState.getMinionById(minionIDs.get(0)).y;
        int goodMinions = 0;

        for (Integer id : minionIDs) {
            Unit minion = inputState.getMinionById(id);
            if (minion != null) {
                goodMinions++;
                centerX += minion.getX();
                centerY += minion.getY();
            }
        }

        if (goodMinions > 0) {
            centerX = centerX / goodMinions;
            centerY = centerY / goodMinions;

            for (Integer id : minionIDs) {
                Unit minion = inputState.getMinionById(id);
                if (minion != null) {

                    inputState.moveMinion(x - centerX, y - centerY, minion);
                }
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "MoveMinionMessage: " + minionIDs + " playerID: " + playerID;
    }
}
