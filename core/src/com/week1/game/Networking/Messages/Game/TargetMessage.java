package com.week1.game.Networking.Messages.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.week1.game.InfoUtil;
import com.week1.game.Model.Entities.Unit;
import com.week1.game.Model.GameEngine;
import com.week1.game.Model.GameState;
import com.week1.game.Networking.Messages.MessageType;

public class TargetMessage extends GameMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.TARGET;

    public Array<Integer> minionIDs = new Array<>();
    public int targetID;

    public TargetMessage(Array<Unit> minions, int targetID, int playerID, int intHash) {
        super(playerID, MESSAGE_TYPE, intHash);
        this.targetID = targetID;
        for (int i = 0; i < minions.size; i++) {
            minionIDs.add(minions.get(i).ID);
        }
    }

    @Override
    public boolean process(GameEngine engine, GameState gameState, InfoUtil util) {
        Gdx.app.log("TargetMessage", "Processing TargetMessage");
        // To make things simple, we'll just pass the game state this entire message.
        gameState.changeTarget(this);
        return true;
    }
}
