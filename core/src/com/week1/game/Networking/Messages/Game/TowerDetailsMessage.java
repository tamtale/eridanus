package com.week1.game.Networking.Messages.Game;

import com.badlogic.gdx.utils.Array;
import com.week1.game.InfoUtil;
import com.week1.game.Model.GameState;
import com.week1.game.Model.TowerDetails;
import com.week1.game.Model.TowerInfo;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.TowerBuilder.BlockSpec;

import java.util.List;


public class TowerDetailsMessage extends GameMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.TOWERDETAILS;
    private final static String TAG = "TowerDetailsMessage";
    
    private List<List<List<BlockSpec>>> details;
    
    public TowerDetailsMessage(int playerID, List<List<List<BlockSpec>>> details) {
        super(playerID, MESSAGE_TYPE);
        this.details = details;
        
    }

    @Override
    public boolean process(GameState inputState, InfoUtil util){
        System.out.println("Processing TowerDetailsMessage!");
        inputState.setTowerInfo(new TowerInfo(details));
        return true;
    }

    @Override
    public String toString() {
        return "TowerDetailsMessage: " + details;
    }
}
