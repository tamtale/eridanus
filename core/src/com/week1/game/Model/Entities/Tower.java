package com.week1.game.Model.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.Model.Components.*;
import com.week1.game.Model.GameState;
import com.week1.game.Model.World.Block;
import com.week1.game.TowerBuilder.BlockSpec;
import com.week1.game.TowerBuilder.TowerDetails;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.week1.game.Model.StatsConfig.*;

public class Tower {
    public int ID;
    private static final Random r = new Random(123456789);
    private PositionComponent positionComponent;
    private HealthComponent healthComponent;
    private DamagingComponent damagingComponent;
    private OwnedComponent ownedComponent;
    private TargetingComponent targetingComponent;
    protected int towerType;
    protected double dmg;
    protected double range;
    protected double cost;
    private List<BlockSpec> layout;
    private Map<Integer, Integer> spawnerCounts;
    public Vector3 highestBlockLocation;
    
    public Tower(
        PositionComponent positionComponent,
        HealthComponent healthComponent,
        DamagingComponent damagingComponent,
        TargetingComponent targetingComponent,
        OwnedComponent ownedComponent,
        ManaRewardComponent manaRewardComponent,
        VisibleComponent visibleComponent,
        TowerDetails towerDetails,
        int towerType,
        int ID
    ) {
        this.positionComponent = positionComponent;
        this.healthComponent = healthComponent;
        this.ownedComponent = ownedComponent;
        this.targetingComponent = targetingComponent;
        this.damagingComponent = damagingComponent;
        this.dmg = towerDetails.getAtk();
        this.cost = towerDetails.getPrice();
        this.range = towerDetails.getRange();
        this.towerType = towerType;
        this.highestBlockLocation = new Vector3(positionComponent.position).add(towerDetails.getHighestBlock().x, towerDetails.getHighestBlock().z, towerDetails.getHighestBlock().y);
        this.layout = towerDetails.getLayout();
        this.spawnerCounts = towerDetails.getSpawnerCounts();
        this.ID = ID;
    }

    public float getX() {
        return this.positionComponent.position.x;
    }

    public float getY() {
        return this.positionComponent.position.y;
    }

    public float getZ() {
        return this.positionComponent.position.z;
    }

    public int getPlayerId(){return ownedComponent.playerID;}

    public List<BlockSpec> getLayout() {
        return layout;
    }
    
    final private int spawnerInterval = 50;
    final private int effectOffset = r.nextInt(spawnerInterval);
    public void doSpecialEffect(int communicationTurn, GameState state) {
        
        // Spawner special effect
        if (!spawnerCounts.keySet().isEmpty() && (communicationTurn + effectOffset) % spawnerInterval == 0) {
            int minionX = -1;
            int minionY = -1;
            int minionZ = -1;
            
            int[] dims = state.getWorld().getWorldDimensions();
            
            int minX = Math.max((int) positionComponent.position.x - 1, 0);
            int maxX = Math.min((int) positionComponent.position.x + 1, dims[0] - 1);
            int minY = Math.max((int) positionComponent.position.y - 1, 0);
            int maxY = Math.min((int) positionComponent.position.y + 1, dims[1] - 1);
            
            // At each interval, spawn a minion for each spawner block in the tower
            // TODO: spawn different minions for  different types of spawner block
            for (int spawnerType : spawnerCounts.keySet()) {
                for (int i = 0; i < spawnerCounts.get(spawnerType); i++) {
                    while (true) {
                        // Generate random coordinates near the spawner tower
                        minionX = r.nextInt(maxX - minX) + minX;
                        minionY = r.nextInt(maxY - minY) + minY;
                        for (int potentialZ = 0; potentialZ < dims[2]; potentialZ++) {
                            if (state.getWorld().getBlock(minionX, minionY, potentialZ) != Block.TerrainBlock.AIR) {
                                minionZ = potentialZ;
                            }
                        }
                        minionZ++; // Minion should go on top of the highest non-air block

                        // Make sure the z coordinate is on the map
                        if (dims[2] <= minionZ || minionZ < 0) {
                            continue; // generate new coordinates
                        }

                        // Make sure supporting block is an okay place to spawn
                        if (!state.getWorld().getBlock(minionX, minionY, minionZ - 1).allowMinionToSpawnOn()) {
                            continue; // generate new coordinates
                        }

                        break; // The random coordinates generated are acceptable; spawn the minion
                    }

                    Gdx.app.debug("Tower- lji1", "About to spawn a minion at: " + minionX + ", " + minionY + ", " + minionZ);

                    int minXMove = Math.max((int) positionComponent.position.x - 5, 0);
                    int maxXMove = Math.min((int) positionComponent.position.x + 5, dims[0] - 1);
                    int minYMove = Math.max((int) positionComponent.position.y - 5, 0);
                    int maxYMove = Math.min((int) positionComponent.position.y + 5, dims[1] - 1);
                    int moveX  = r.nextInt(maxXMove - minXMove) + minXMove;
                    int moveY = r.nextInt(maxYMove - minYMove) + minYMove;
                    
                    // Create and place the minion
                    final float finalMinionX = minionX;
                    final float finalMinionY = minionY;
                    final float finalMinionZ = minionZ;
                    
                    final float finalMoveX = moveX;
                    final float finalMoveY = moveY;

                    Unit unit = state.addUnit(finalMinionX, finalMinionY, finalMinionZ, (float) tempMinion1Health, ownedComponent.playerID);
                    state.moveMinion(finalMoveX,finalMoveY, unit);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Tower{" +
                "x=" + positionComponent.position.x +
                ", y=" + positionComponent.position.y +
                ", playerID=" + ownedComponent.playerID +
                ", towerType=" + towerType +
                ", hp=" + healthComponent.curHealth +
                ", maxHp=" + healthComponent.maxHealth +
                ", dmg=" + dmg +
                ", range=" + range +
                ", cost=" + cost +
                '}';
    }
    
    public PositionComponent getPositionComponent() {
        return positionComponent;
    }
    public HealthComponent getHealthComponent() {return healthComponent;}
    public OwnedComponent getOwnedComponent(){return ownedComponent;}
    public TargetingComponent getTargetingComponent() {
        return targetingComponent;
    }
    public DamagingComponent getDamagingComponent() {
        return damagingComponent;
    }
}
