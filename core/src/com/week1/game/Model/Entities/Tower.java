package com.week1.game.Model.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.Components.*;
import com.week1.game.Model.Damage;
import com.week1.game.Model.GameState;
import com.week1.game.Model.World.Block;
import com.week1.game.TowerBuilder.BlockSpec;
import com.week1.game.TowerBuilder.TowerDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.week1.game.Model.StatsConfig.*;

public class Tower extends Building implements Damaging {
    public int ID;
    private static final int SIDELENGTH = 3;
    private static final Random r = new Random(123456789);
    private PositionComponent positionComponent;
    private HealthComponent healthComponent;
    private DamagingComponent damagingComponent;
    private OwnedComponent ownedComponent;
    private TargetingComponent targetingComponent;
    private static Texture skin; // TODO change this when we go to 3D to actually use the model of the tower.
    protected int towerType;
    private final static Map<Integer, Texture> colorMap = new HashMap<>();
    protected double dmg;
    protected double range;
    protected double cost;
    private List<BlockSpec> layout;
    private Map<Vector3, Array<Connection<Vector3>>> removedEdges = new HashMap<>();
    private Map<Integer, Integer> spawnerCounts;
    public Vector3 highestBlockLocation;
    
    public Tower(
        PositionComponent positionComponent,
        HealthComponent healthComponent,
        DamagingComponent damagingComponent,
        TargetingComponent targetingComponent,
        OwnedComponent ownedComponent,
        ManaRewardComponent manaRewardComponent,
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

    @Override
    public boolean takeDamage(Damaging attacker, double dmg, Damage.type damageType) {
        this.healthComponent.curHealth -= dmg;
        if (this.healthComponent.curHealth <= 0) {
            return true;
            // TODO probably need to send something to engine more than just returning true
        } else {
            return false;
        }
    }

    @Override
    public float getX() {
        return this.positionComponent.position.x;
    }

    @Override
    public float getY() {
        return this.positionComponent.position.y;
    }

    @Override
    public float getZ() {
        return this.positionComponent.position.z;
    }

    @Override
    public float getCurrentHealth() {
        return healthComponent.curHealth;
    }

    @Override
    public float getMaxHealth() {
        return healthComponent.maxHealth;
    }

    @Override
    public boolean isDead() {
        return this.healthComponent.curHealth <= 0;
    }

    @Override
    public boolean hasTargetInRange(Damageable victim) {
        return Math.sqrt(Math.pow(this.positionComponent.position.x - victim.getX(), 2) + Math.pow(this.positionComponent.position.y - victim.getY(), 2)) < range;
    }

    @Override
    public double getDamage() {
        return this.dmg;
    }

    @Override
    public int getPlayerId(){return ownedComponent.playerID;}

    @Override
    public void getPos(Vector3 pos) {
        pos.set(positionComponent.position.x, positionComponent.position.y, positionComponent.position.z);
    }

    @Override
    public float getReward() {
        return (float) cost * (float) towerDestructionBonus;
    }


    @Override
    public <T> T accept(DamageableVisitor<T> visitor) {
        return visitor.acceptTower(this);
    }

    public int getSidelength(){
        return SIDELENGTH;
    }

    @Override
    public boolean overlap(float x, float y) {
        int startX = (int) this.positionComponent.position.x - getSidelength()/2;
        int startY = (int) this.positionComponent.position.y - getSidelength()/2;
        int endX = startX + getSidelength();
        int endY = startY + getSidelength();
        return (x > startX && x < endX && y > startY && y < endY);
    }

    @Override
    public Vector3 closestPoint(float x, float y) {
        int startX = (int) this.positionComponent.position.x - getSidelength()/2;
        int startY = (int) this.positionComponent.position.y - getSidelength()/2;
        int endX = startX + getSidelength();
        int endY = startY + getSidelength();

        if (x < startX && y < startY) {
            return new Vector3(startX, startY, 0);
        }
        else if (x < startX && y > startY && y < endY){
            return new Vector3(startX, y, 0);
        }
        else if (x < startX && y > endY) {
            return new Vector3(startX, endY, 0);
        }
        else if (x > startX && x < endX && y > endY) {
            return new Vector3(x, endY, 0);
        }
        else if (x > endX && y > endY) {
            return new Vector3(endX, endY, 0);
        }
        else if (x > endX && y > startY && y < endY) {
            return new Vector3(endX, y, 0);
        }
        else if (x > endX && y < startY) {
            return new Vector3(endX, startY, 0);
        }
        else{
            return new Vector3(x, startY, 0);
        }
    }

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
                    Gdx.app.postRunnable(() -> {
                        Unit unit = state.addUnit(finalMinionX, finalMinionY, finalMinionZ, (float) tempMinion1Health, ownedComponent.playerID);
                        state.moveMinion(finalMoveX,finalMoveY, unit);
                    });
                }
            }
        }
    }

    @Override
    public void putRemovedEdges(Vector3 fromNode, Array<Connection<Vector3>> connections) {
        removedEdges.put(fromNode, connections);
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
    
    @Override
    public void getDisplayPos(Vector3 displayPos) {
        displayPos.set(highestBlockLocation);
    }

    public int getPlayerID() {
        return ownedComponent.playerID;
    }

    public PositionComponent getPositionComponent() {
        return positionComponent;
    }
}
