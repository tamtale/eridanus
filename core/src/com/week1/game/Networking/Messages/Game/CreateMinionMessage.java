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
        
        if (!inputState.getWorld().getBlock((int)x, (int)y, (int)z - 1).allowMinionToSpawnOn()) {
            util.log("lji1 - CreateMinionMessage", "Not allowed to spawn minion on tower.");
            return false;
        }


        inputState.getPlayerStats(playerID).useMana(tempMinion1Cost);

        Gdx.app.postRunnable(() -> {
            util.log("pjb3 - CreateMinionMessage", "Used " + tempMinion1Cost + " mana to create minion.");
            inputState.addUnit(x, y, z, (float) tempMinion1Health, playerID);
        });
        return true;
    }

    @Override
    public String toString() {
        return "CreateMinionMessage: " + x + ", " + y + ", " + unitType + ", " + playerID;
    }
}
