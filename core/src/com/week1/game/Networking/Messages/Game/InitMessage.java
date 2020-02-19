package com.week1.game.Networking.Messages.Game;

import com.week1.game.Model.GameState;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.InfoUtil;

public class InitMessage extends GameMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.INIT;
    private final static String TAG = "InitMessage";
    
    private int numPlayers;

    public InitMessage(int numPlayers, int playerID, int intHash){
        super(playerID, MESSAGE_TYPE, intHash);
        this.numPlayers = numPlayers;
    }

    @Override
    public boolean process(GameState inputState, InfoUtil util){
        inputState.initializeGame(this.numPlayers);
        return true;
    }

    @Override
    public String toString() {
        return "InitMessage: numPlayers: " + numPlayers;
    }
}
