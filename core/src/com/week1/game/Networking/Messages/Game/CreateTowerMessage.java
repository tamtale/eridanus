package com.week1.game.Networking.Messages.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.week1.game.InfoUtil;
import com.week1.game.Model.Entities.Tower;
import com.week1.game.Model.Entities.Unit;
import com.week1.game.Model.GameEngine;
import com.week1.game.Model.GameState;
import com.week1.game.Model.World.Block;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.TowerBuilder.BlockSpec;
import com.week1.game.TowerBuilder.TowerDetails;


public class CreateTowerMessage extends GameMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.CREATETOWER;

    private float x, y, z; // The coordinates of the center of the tower (the core block is the center)
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
        Gdx.app.debug("CreateTowerMessage", "Processing CreateTowerMessage!");

        TowerDetails towerDetails = inputState.getTowerDetails(this.playerID, this.towerType);
        double towerCost;
        towerCost = towerDetails.getPrice();

        // Does the player have enough mana
        if (towerCost > inputState.getPlayerStats(playerID).getMana()) {
            // Do not have enough mana!
            util.log("pjb3 - CreateTowerMessage", "Not enough mana to create tower. Need " + towerCost);
            return false; // indicate it was NOT placed
        }

        // The tower can't be hanging off the edge of the map
        // The tower can't be overlapping with an existing structure
        // The tower's base must be fully supported by terrain blocks
        // The tower can't be overlapping with an existing minion
        if (!checkTowerBlockPlacement(inputState, towerDetails, util)){
            return false;
        }

        // The tower can't be too far from an existing friendly structure
        if (!inputState.findNearbyStructure(x, y, z, playerID)) {
            util.log("pjb3 - CreateTowerMessage", "Not close enough to an existing tower or home base");
            return false;
        }

        // Deduct the mana cost from the creating player
        util.log("pjb3 - CreateTowerMessage", "Used " + towerCost + " mana to create tower.");
        inputState.getPlayerStats(playerID).useMana(towerCost);

        // Only create the tower once we're sure it's safe to do so
        inputState.addTower((int) x, (int) y, (int) z, towerDetails, playerID, towerType);

        return true;
    }

    @Override
    public String toString() {
        return "CreateTowerMessage: " + x + ", " + y + ", " + towerType + ", " + playerID;
    }


    /*
        Checks that the tower is completely supported by the map
        and that it doesn't overlap with an existing blocks
     */
    private boolean checkTowerBlockPlacement(GameState inputState, TowerDetails towerDetails, InfoUtil util) {

        int[] dimensions = inputState.getWorld().getWorldDimensions();
        int maxX = dimensions[0];
        int maxY = dimensions[1];
        int maxZ = dimensions[2];

        int tempX;
        int tempY;
        int tempZ;

        Array<Unit> minions = inputState.getUnits();

        for(BlockSpec bs : towerDetails.getLayout()) {
            tempX = (int)(x + bs.getX());
            tempY = (int)(y + bs.getZ());
            tempZ = (int)(z + bs.getY()); // Notice that x,z are the flat coords and y is for height

            if (!((0 <= tempX && tempX < maxX) &&
                    (0 <= tempY && tempY < maxY) &&
                    (0 <= tempZ && tempZ < maxZ))) {
                util.log("lji1 - CreateTowerMessage", "Can't build tower off the map.");
                return false;
            }
            if (inputState.getWorld().getBlock(tempX, tempY, tempZ) != Block.TerrainBlock.AIR) {
                util.log("lji1 - CreateTowerMessage", "Can't build a tower overlapping with existing blocks");
                return false;
            }

            if (bs.getY() == 0 && !(inputState.getWorld().getBlock(tempX, tempY, tempZ - 1).canSupportTower())) {
                util.log("lji1 - CreateTowerMessage", "Tower not fully supported by terrain");
                return false;
            }

            for(int u = 0; u < minions.size; u++) {
                Unit minion = minions.get(u);
                if ((((int)minion.getX() == tempX) || ((int)minion.getX() + 1 == tempX)) &&
                        (((int)minion.getY() == tempY) || ((int)minion.getY() + 1 == tempY))) {
                    util.log("lji1 - CreateTowerMessage", "Tower overlaps with minion.");
                    return false;
                }
            }
        }

        return true;
    }
}
