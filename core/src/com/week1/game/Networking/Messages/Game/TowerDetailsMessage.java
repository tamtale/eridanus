package com.week1.game.Networking.Messages.Game;

import com.badlogic.gdx.Gdx;
import com.week1.game.InfoUtil;
import com.week1.game.Model.GameEngine;
import com.week1.game.Model.GameState;
import com.week1.game.Model.TowerLoadouts;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.TowerBuilder.BlockSpec;

import java.util.List;


public class TowerDetailsMessage extends GameMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.TOWERDETAILS;
    private final static String TAG = "TowerDetailsMessage";
    
    private List<List<List<BlockSpec>>> details;
    
    public TowerDetailsMessage(int playerID, List<List<List<BlockSpec>>> details, int intHash) {
        super(playerID, MESSAGE_TYPE, intHash);
        this.details = details;
        
    }

    @Override
    public boolean process(GameEngine engine, GameState inputState, InfoUtil util){
        Gdx.app.log("TowerDetailsMessage", "Processing TowerDetailsMessage!");
        inputState.setTowerInfo(new TowerLoadouts(details));
        
        // The tower details message is the last initialization message sent by the host, so
        // start the engine
        engine.start();
        
        return true;
    }

    @Override
    public String toString() {
        return "TowerDetailsMessage: " + details;
    }
}
