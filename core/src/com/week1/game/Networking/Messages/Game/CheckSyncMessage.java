package com.week1.game.Networking.Messages.Game;


import com.week1.game.InfoUtil;
import com.week1.game.Model.GameEngine;
import com.week1.game.Model.GameState;
import com.week1.game.Networking.Messages.MessageType;


/**
 * This message is sent every 10 turns to the host. It contains the intHash of the state that SHOULD be
 * consistent across all clients
 */
public class CheckSyncMessage extends GameMessage {
    private int turn;

    public CheckSyncMessage(int playerID, MessageType messageTypeID, int intHash, int turn) {
        super(playerID, messageTypeID, intHash);
        this.turn = turn;
    }

    public int getTurn() {
        return turn;
    }

    @Override
    public boolean process(GameEngine engine, GameState gameState, InfoUtil util) {
        return true;
    }
}
