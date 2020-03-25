package com.week1.game.Networking.Messages.Game;

import com.badlogic.gdx.Gdx;
import com.week1.game.InfoUtil;
import com.week1.game.Model.GameEngine;
import com.week1.game.Model.GameState;
import com.week1.game.Model.TowerLite;
import com.week1.game.Model.TowerLoadouts;
import com.week1.game.Networking.Messages.MessageType;

import java.util.List;
import java.util.Map;


public class TowerDetailsMessage extends GameMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.TOWERDETAILS;
    private final static String TAG = "TowerDetailsMessage";
    
    private Map<Integer, List<TowerLite>> details;
    // player id maps to a list of names of towers paired with tower structure
    
    public TowerDetailsMessage(int playerID, Map<Integer, List<TowerLite>> details, int intHash) {
        super(playerID, MESSAGE_TYPE, intHash);
        this.details = details;
    }

    @Override
    public boolean process(GameEngine engine, GameState inputState, InfoUtil util){
        Gdx.app.log("TowerDetailsMessage", "Processing TowerDetailsMessage!");
        inputState.setTowerInfo(new TowerLoadouts(details));
        return true;
    }

    @Override
    public String toString() {
        return "TowerDetailsMessage: " + details;
    }
}
