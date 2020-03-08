package com.week1.game.Networking.Messages.Game;

import com.week1.game.Model.GameEngine;
import com.week1.game.Model.GameState;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.InfoUtil;

public class InitMessage extends GameMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.INIT;
    private final static String TAG = "InitMessage";

    private long mapSeed;
    private int numPlayers;

    public InitMessage(long mapSeed, int numPlayers, int playerID, int intHash){
        super(playerID, MESSAGE_TYPE, intHash);
        this.mapSeed = mapSeed;
        this.numPlayers = numPlayers;
    }

    @Override
    public boolean process(GameEngine engine, GameState inputState, InfoUtil util){
        
        inputState.initializeGame(mapSeed, this.numPlayers);
        // The InitMessage message is the last initialization message sent by the host, so start the engine.
        engine.start();
        return true;
    }

    @Override
    public String toString() {
        return "InitMessage: numPlayers: " + numPlayers;
    }
}
