package com.week1.game.Networking.Messages.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.week1.game.Model.Damage;
import com.week1.game.Model.GameEngine;
import com.week1.game.Model.GameState;
import com.week1.game.Model.Entities.Tower;
import com.week1.game.Model.World.Block;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Model.Entities.TowerType;
import com.week1.game.InfoUtil;
import com.week1.game.TowerBuilder.TowerDetails;


public class CreateTowerMessage extends GameMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.CREATETOWER;

    private float x, y;
    private TowerType towerType;


    public CreateTowerMessage(float x, float y, TowerType towerType, int playerID, int intHash) {
        super(playerID, MESSAGE_TYPE, intHash);
        this.x = x;
        this.y = y;
        this.towerType = towerType;
    }

    @Override
    public boolean process(GameEngine engine, GameState inputState, InfoUtil util){
        Gdx.app.log("CreateTowerMessage", "Processing CreateTowerMessage!");

        inputState.getWorld().setBlock(2,2,2, Block.TowerBlock.WATERGUN);

        Gdx.app.log("CreateTowerMessage", "Should have created extra block");
        
        TowerDetails towerDetails = inputState.getTowerDetails(this.playerID, this.towerType.ordinal());
        double towerCost, towerHealth, towerDmg, towerRange;
        towerCost = towerDetails.getPrice();
        towerHealth = towerDetails.getHp();
        towerDmg = towerDetails.getAtk();
        towerRange = towerDetails.getRange();

        if (towerCost > inputState.getPlayerStats(playerID).getMana()) {
            // Do not have enough mana!
            util.log("pjb3 - CreateTowerMessage", "Not enough mana to create tower. Need " + towerCost);
            return false; // indicate it was NOT placed
        }

        // Test to see if it is in the proximity of a tower or a home base
        if (!inputState.findNearbyStructure(x, y, playerID)) {
            util.log("pjb3 - CreateTowerMessage", "Not close enough to an existing tower or home base");
            return false;
        }
        
        if(inputState.overlapsExistingStructure(this.playerID, towerType.ordinal(), (int)x, (int)y)) {
            util.log("lji1 - CreateTowerMessage", "Overlapping with existing structure.");
            return false;
        }

        util.log("pjb3 - CreateTowerMessage", "Used " + towerCost + " mana to create tower.");
        inputState.getPlayerStats(playerID).useMana(towerCost);


        util.log("lji1 - CreateTowerMessage", "Creating tower!");
        Tower tower = new Tower((int) x, (int) y, towerHealth, towerDmg, towerRange, Damage.type.BASIC, towerCost, playerID, towerType.ordinal());

        inputState.addTower(tower, playerID);
        
        
        
        return true;
    }

    @Override
    public String toString() {
        return "CreateTowerMessage: " + x + ", " + y + ", " + towerType + ", " + playerID;
    }
}
