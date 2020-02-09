package com.week1.game.Networking.Messages.Game;

import com.badlogic.gdx.utils.Array;
import com.week1.game.InfoUtil;
import com.week1.game.Model.GameState;
import com.week1.game.Model.TowerDetails;
import com.week1.game.Model.TowerInfo;
import com.week1.game.Networking.Messages.MessageType;

import java.util.Map;

public class TowerDetailsMessage extends GameMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.TOWERDETAILS;
    private final static String TAG = "TowerDetailsMessage";
    
    private TowerDetails[][] details;
    
    public TowerDetailsMessage(int playerID, TowerDetails[][] details) {
        super(playerID, MESSAGE_TYPE);
        this.details = details;
        
    }

    @Override
    public boolean process(GameState inputState, InfoUtil util){
        inputState.setTowerInfo(new TowerInfo(details));
        return true;
    }

    @Override
    public String toString() {
        return "TowerDetailsMessage: " + details;
    }
}
