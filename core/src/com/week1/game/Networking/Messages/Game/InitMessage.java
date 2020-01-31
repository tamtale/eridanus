package com.week1.game.Networking.Messages.Game;

import com.week1.game.Model.GameState;
import com.week1.game.Model.Unit;
import com.week1.game.Networking.Messages.MessageType;

public class InitMessage extends GameMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.INIT;
    private final static String TAG = "InitMessage";
    
    private int numPlayers;

    public InitMessage(int numPlayers, int playerID){
        super(playerID, MESSAGE_TYPE);
        this.numPlayers = numPlayers;
    }

    @Override
    public boolean process(GameState inputState){
        inputState.setNumPlayers(this.numPlayers);
        return true;
    }

    @Override
    public String toString() {
        return "InitMessage: numPlayers: " + numPlayers;
    }
}
