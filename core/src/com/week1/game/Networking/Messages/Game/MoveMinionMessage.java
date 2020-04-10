package com.week1.game.Networking.Messages.Game;

import com.badlogic.gdx.Gdx;
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
    private Array<Integer> minionIDs = new Array<>();

    public MoveMinionMessage(float x, float y, int playerID, Array<Unit> minions, int intHash) {
        super(playerID, MESSAGE_TYPE, intHash);
        this.x = x;
        this.y = y;

        minions.forEach((minion) -> minionIDs.add(minion.ID));
    }

    @Override
    public boolean process(GameEngine engine, GameState inputState, InfoUtil util){
        float centerX = 0;//x - inputState.getMinionById(minionIDs.get(0)).x;
        float centerY = 0;//y - inputState.getMinionById(minionIDs.get(0)).y;
        Unit[] goodMinions = new Unit[minionIDs.size];
        int m = 0;
        for (Integer id : minionIDs) {
            Unit minion = inputState.getMinionById(id);
            if (minion != null) {
                goodMinions[m] = minion;
                m++;
            }
        }
        Gdx.app.log("wab2 - MoveMinionMessage", "number of goodMinions " + goodMinions.length);
        if (goodMinions.length > 0) {
            int k = 0;
            int worldXSize = inputState.getWorld().getWorldDimensions()[0];
            int worldYSize = inputState.getWorld().getWorldDimensions()[1];
            int dim = (int) Math.ceil(Math.sqrt(goodMinions.length));
            int xStart = (int) (x - dim/2);
            int yStart = (int) (y - dim/2);
            for (int i = xStart; i < xStart + dim; i++){
                for (int j = yStart; j < yStart + dim; j++){
                    if (k > goodMinions.length - 1){
                        break;
                    }
                    i = Math.max(i, 0);
                    i = Math.min(worldXSize, i);
                    j = Math.max(j, 0);
                    j = Math.min(worldYSize, j);
                    Unit minion = goodMinions[k];
                    if (minion != null) {
                        inputState.moveMinion(i, j, minion);
                    } else {
                        i--;
                        j--;
                    }
                    k++;
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
