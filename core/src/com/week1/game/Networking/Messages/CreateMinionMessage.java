package com.week1.game.Networking.Messages;

import com.badlogic.gdx.math.Vector2;
import com.week1.game.Model.GameState;
import com.week1.game.Model.Unit;
import com.week1.game.Networking.MessageTypes;
import com.week1.game.AIMovement.SteeringAgent;

import java.awt.*;

public class CreateMinionMessage extends AMessage {
    private final static int MESSAGE_TYPE = MessageTypes.CREATE.ordinal();

    private float x, y;
    private int unitType;

    public CreateMinionMessage(float x, float y, int unitType, int playerID){
        super(playerID, MESSAGE_TYPE);
        this.x = x;
        this.y = y;
        this.unitType = unitType; // TODO use this
    }

    @Override
    public boolean process(GameState inputState){
        Unit unit = new Unit(x, y);
        inputState.addUnit(unit);
//        SteeringAgent agent = new SteeringAgent(unit, new Vector2(x, y), 0,
//                new Vector2((float) .1, (float) .1), 0, 1, true, (float).5);
//        inputState.addAgent(agent);
//        unit.agent = agent;
        return true;
    }
    
    @Override
    public String toString() {
        return "CreateMinionMessage: " + x + ", " + y + ", " + unitType + ", " + playerID;
    }
}
