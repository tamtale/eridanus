package com.week1.game.Networking.Messages.Game;

import com.week1.game.Model.GameState;
import com.week1.game.Model.Entities.Unit;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.InfoUtil;

import static com.week1.game.Model.StatsConfig.*;

public class CreateMinionMessage extends GameMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.CREATEMINION;
    private final static String TAG = "CreateMinionMessage";
    

    private float x, y;
    private int unitType;

    public CreateMinionMessage(float x, float y, int unitType, int playerID){
        super(playerID, MESSAGE_TYPE);
        this.x = x;
        this.y = y;
        this.unitType = unitType; // TODO use this
    }

    @Override
    public boolean process(GameState inputState, InfoUtil util){
        // First, check if it is able to be created.
        // TODO do lookup of the cost based on unitType, do not use hardcoded number [tempMinion1Cost/tempMinion1Health]
        if (tempMinion1Cost > inputState.getPlayerStats(playerID).getMana()) {
            // Do not have enough mana!
            util.log("pjb3 - CreateMinionMessage", "Not enough mana (" +
                    inputState.getPlayerStats(playerID).getMana() + ") to create unit of cost " + tempMinion1Cost);
            return false; // indicate it was NOT placed
        }

        // Test to see if it is in the proximity of a tower or a home base
        if (!inputState.findNearbyStructure(x, y, playerID)) {
            util.log("pjb3 - CreateMinionMessage", "Not close enough to an existing tower or home base");
             return false;
        }


        util.log("pjb3 - CreateMinionMessage", "Used " + tempMinion1Cost + " mana to create minion.");
        inputState.getPlayerStats(playerID).useMana(tempMinion1Cost);

        Unit unit = new Unit(x, y, null, tempMinion1Health, playerID);
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
