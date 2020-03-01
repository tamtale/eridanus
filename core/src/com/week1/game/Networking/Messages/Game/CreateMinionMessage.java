package com.week1.game.Networking.Messages.Game;

import com.badlogic.gdx.Gdx;
import com.week1.game.Model.Entities.Building;
import com.week1.game.Model.GameEngine;
import com.week1.game.Model.GameState;
import com.week1.game.Model.Entities.Unit;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.InfoUtil;

import static com.week1.game.Model.StatsConfig.*;

public class CreateMinionMessage extends GameMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.CREATEMINION;
    private final static String TAG = "CreateMinionMessage";
    

    private float x, y, z;
    private int unitType;

    // Holdover form 2D-land.
    public CreateMinionMessage(float x, float y, int unitType, int playerID, int intHash){
      this(x, y, 1, unitType, playerID, intHash);
    }

    public CreateMinionMessage(float x, float y, float z, int unitType, int playerID, int intHash){
        super(playerID, MESSAGE_TYPE, intHash);
        this.x = x;
        this.y = y;
        this.unitType = unitType; // TODO use this
        this.z = z;
    }

    @Override
    public boolean process(GameEngine engine, GameState inputState, InfoUtil util){
        // First, check if it is able to be created.
        // TODO do lookup of the cost based on unitType, do not use hardcoded number [tempMinion1Cost/tempMinion1Health]
        if (tempMinion1Cost > inputState.getPlayerStats(playerID).getMana()) {
            // Do not have enough mana!
            util.log("pjb3 - CreateMinionMessage", "Not enough mana (" +
                    inputState.getPlayerStats(playerID).getMana() + ") to create unit of cost " + tempMinion1Cost);
            return false; // indicate it was NOT placed
        }

        // Test to see if it is in the proximity of a tower or a home base
        if (!inputState.findNearbyStructure(x, y, z, playerID)) {
            util.log("pjb3 - CreateMinionMessage", "Not close enough to an existing tower or home base");
             return false;
        }
        
        // Test to see if the minion is going to be placed on top of a tower or base
        for (Building building : inputState.getBuildings()) {
            if (building.overlap(this.x, this.y)) {
                util.log("lji1 - CreateMinionMessage", "Overlapping with base or tower.");
                return false;
            }
        }


        inputState.getPlayerStats(playerID).useMana(tempMinion1Cost);

        Gdx.app.postRunnable(() -> {
            Unit unit = new Unit(x, y, z, tempMinion1Health, playerID);
            util.log("pjb3 - CreateMinionMessage", "Used " + tempMinion1Cost + " mana to create minion.");
            inputState.addUnit(unit);
        });
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
