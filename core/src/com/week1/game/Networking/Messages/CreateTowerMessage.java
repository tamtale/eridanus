package com.week1.game.Networking.Messages;

import com.week1.game.Model.GameState;
import com.week1.game.Model.Unit;

public class CreateTowerMessage extends GameMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.CREATETOWER;

    private float x, y;
    private TowerType towerType;

    public CreateTowerMessage(float x, float y, TowerType towerType, int playerID){
        super(playerID, MESSAGE_TYPE);
        this.x = x;
        this.y = y;
        this.towerType = towerType; // TODO use this
    }

    @Override
    public boolean process(GameState inputState){
        // TODO: implement
//        Unit unit = new Unit(x, y);
//        inputState.addUnit(unit);
        return true;
    }

    @Override
    public String toString() {
        return "CreateTowerMessage: " + x + ", " + y + ", " + towerType + ", " + playerID;
    }
}
