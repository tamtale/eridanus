package com.week1.game.Networking.Messages.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.week1.game.Model.Damage;
import com.week1.game.Model.GameEngine;
import com.week1.game.Model.GameState;
import com.week1.game.Model.Entities.Tower;
import com.week1.game.Model.Initializer;
import com.week1.game.Model.World.Block;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Model.Entities.TowerType;
import com.week1.game.InfoUtil;
import com.week1.game.TowerBuilder.BlockSpec;
import com.week1.game.TowerBuilder.TowerDetails;

import java.util.List;


public class CreateTowerMessage extends GameMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.CREATETOWER;

    private float x, y, z; // The coordinates of the center of the tower
    private int towerType;


    public CreateTowerMessage(float x, float y, float z, int towerType, int playerID, int intHash) {
        super(playerID, MESSAGE_TYPE, intHash);
        this.x = x;
        this.y = y;
        this.z = z;
        this.towerType = towerType;
    }

    @Override
    public boolean process(GameEngine engine, GameState inputState, InfoUtil util){
        Gdx.app.log("CreateTowerMessage", "Processing CreateTowerMessage!");


        TowerDetails towerDetails = inputState.getTowerDetails(this.playerID, this.towerType);
        double towerCost, towerHealth, towerDmg, towerRange;
        towerCost = towerDetails.getPrice();
        towerHealth = towerDetails.getHp();
        towerDmg = towerDetails.getAtk();
        towerRange = towerDetails.getRange();

        
       
        // Does the player have enough mana
        if (towerCost > inputState.getPlayerStats(playerID).getMana()) {
            // Do not have enough mana!
            util.log("pjb3 - CreateTowerMessage", "Not enough mana to create tower. Need " + towerCost);
            return false; // indicate it was NOT placed
        }
        
        // The tower can't be hanging off the edge of the map
        if (!completelyOnMap(inputState, towerDetails)){
            util.log("lji1 - CreateTowerMessage", "Can't build tower off the map.");
            return false;
        }

        // TODO: The tower can't be too far from an existing friendly structure
//        if (!inputState.findNearbyStructure(x, y, playerID)) {
//            util.log("pjb3 - CreateTowerMessage", "Not close enough to an existing tower or home base");
//            return false;
//        }
        
        // TODO: The tower can't be overlapping with an existing friendly structure
//        if(inputState.overlapsExistingStructure(this.playerID, towerType, (int)x, (int)y)) {
//            util.log("lji1 - CreateTowerMessage", "Overlapping with existing structure.");
//            return false;
//        }

        // Deduct the mana cost from the creating player
        util.log("pjb3 - CreateTowerMessage", "Used " + towerCost + " mana to create tower.");
        inputState.getPlayerStats(playerID).useMana(towerCost);

        // Only create the tower once we're sure it's safe to do so
        Tower tower = new Tower((int) x, (int) y, towerHealth, towerDmg, towerRange, Damage.type.BASIC, towerCost, playerID, towerType);
        inputState.addTower(tower, playerID);
        
        for(BlockSpec bs : towerDetails.getLayout()) {
            inputState.getWorld().setBlock(
                    (int)(x + bs.getX()),
                    (int)(y + bs.getZ()),
                    (int)(z + bs.getY()),
                    Block.TowerBlock.towerBlockMap.get(bs.getBlockCode()));
        }

        return true;
    }

    @Override
    public String toString() {
        return "CreateTowerMessage: " + x + ", " + y + ", " + towerType + ", " + playerID;
    }
    
    
    /*
        Checks that the tower is completely supported by the map
     */
    private boolean completelyOnMap(GameState inputState, TowerDetails towerDetails) {
        int[] dimensions = inputState.getWorld().getWorldDimensions();
        int maxX = dimensions[0];
        int maxY = dimensions[1];
        int maxZ = dimensions[2];

        int tempX;
        int tempY;
        int tempZ;

        for(BlockSpec bs : towerDetails.getLayout()) {
            tempX = (int)(x + bs.getX());
            tempY = (int)(y + bs.getZ()); // Notice that x,z are the flat coords and y is for height
            tempZ = (int)(z + bs.getY());
            if (!((0 <= tempX && tempX < maxX) &&
                    (0 <= tempY && tempY < maxY) &&
                    (0 <= tempZ && tempZ < maxZ))) {
                return false;
            }
        }
        return true;
    }
}
