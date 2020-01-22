package com.week1.game.Networking.Messages;

import com.badlogic.gdx.math.Vector2;
import com.week1.game.Model.GameState;
import com.week1.game.Model.Unit;
import com.week1.game.AIMovement.SteeringAgent;

public class CreateMinionMessage extends AMessage {

    private float x, y;
    private int unitType;

    public CreateMinionMessage(float x, float y, int unitType, int playerID){
        this.x = x;
        this.y = y;
        this.unitType = unitType; // TODO use this
        this.playerID = playerID; // TODO use this
    }

    public boolean process(GameState inputState){
        Unit unit = new Unit(x, y, 0, 0);
        inputState.addUnit(unit);
        SteeringAgent agent = new SteeringAgent(unit, new Vector2(x, y), 0,
                new Vector2((float) .1, (float) .1), 0, 1, true, (float).5);
        inputState.addAgent(agent);
        unit.agent = agent;
        return true;
    }
}
