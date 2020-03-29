package com.week1.game.Model.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.Damage;
import com.week1.game.Model.GameState;
import com.week1.game.Model.World.Block;
import com.week1.game.TowerBuilder.BlockSpec;
import com.week1.game.TowerBuilder.TowerDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.week1.game.Model.StatsConfig.*;

public class Tower extends Building implements Damaging {
    private static final int SIDELENGTH = 3;
    private static final Random r = new Random(123456789);
    public float x, y, z;
    private static Texture skin; // TODO change this when we go to 3D to actually use the model of the tower.
    protected int playerID;
    protected int towerType;
    private final static Map<Integer, Texture> colorMap = new HashMap<>();
    protected double hp;
    protected double maxHp;
    protected double dmg;
    protected double range;
    protected double cost;
    private List<BlockSpec> layout;
    private Map<Vector3, Array<Connection<Vector3>>> removedEdges = new HashMap<>();
    private Map<Integer, Integer> spawnerCounts;
    private Vector3 highestBlockLocation = null;
    
    public Tower(float x, float y, float z, TowerDetails towerDetails, int playerID, int towerType) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.hp = towerDetails.getHp();
        this.maxHp = hp;
        this.dmg = towerDetails.getAtk();
        this.cost = towerDetails.getPrice();
        this.range = towerDetails.getRange();
        this.playerID = playerID;
        this.towerType = towerType;
        this.highestBlockLocation = new Vector3(x,y,z).add(towerDetails.getHighestBlock().x, towerDetails.getHighestBlock().z, towerDetails.getHighestBlock().y);
        this.layout = towerDetails.getLayout();
        this.spawnerCounts = towerDetails.getSpawnerCounts();
    }


    public double getCost() {
        return cost;
    }
    
    public int getTowerType() {
        return towerType;
    }

    public double getHp() { return hp; }

    @Override
    public boolean takeDamage(Damaging attacker, double dmg, Damage.type damageType) {
        this.hp -= dmg;
        if (this.hp <= 0) {
            return true;
            // TODO probably need to send something to engine more than just returning true
        } else {
            return false;
        }
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    @Override
    public float getCurrentHealth() {
        return (float) hp;
    }

    @Override
    public float getMaxHealth() {
        return (float) maxHp;
    }

    @Override
    public boolean isDead() {
        return this.hp <= 0;
    }

    @Override
    public boolean hasTargetInRange(Damageable victim) {
        return Math.sqrt(Math.pow(this.x - victim.getX(), 2) + Math.pow(this.y - victim.getY(), 2)) < range;
    }

    @Override
    public double getDamage() {
        return this.dmg;
    }

    @Override
    public int getPlayerId(){return playerID;}

    @Override
    public void getPos(Vector3 pos) {
        pos.set(x, y, z);
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
        int startX = (int) this.x - getSidelength()/2;
        int startY = (int) this.y - getSidelength()/2;
        int endX = startX + getSidelength();
        int endY = startY + getSidelength();
        return (x > startX && x < endX && y > startY && y < endY);
    }

    @Override
    public Vector3 closestPoint(float x, float y) {
        int startX = (int) this.x - getSidelength()/2;
        int startY = (int) this.y - getSidelength()/2;
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
            
            int minX = Math.max((int)x - 1, 0);
            int maxX = Math.min((int)x + 1, dims[0] - 1);
            int minY = Math.max((int)y - 1, 0);
            int maxY = Math.min((int)y + 1, dims[1] - 1);
            
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

                    int minXMove = Math.max((int)x - 5, 0);
                    int maxXMove = Math.min((int)x + 5, dims[0] - 1);
                    int minYMove = Math.max((int)y - 5, 0);
                    int maxYMove = Math.min((int)y + 5, dims[1] - 1);
                    int moveX  = r.nextInt(maxXMove - minXMove) + minXMove;
                    int moveY = r.nextInt(maxYMove - minYMove) + minYMove;
                    
                    // Create and place the minion
                    final float finalMinionX = minionX;
                    final float finalMinionY = minionY;
                    final float finalMinionZ = minionZ;
                    
                    final float finalMoveX = moveX;
                    final float finalMoveY = moveY;
                    Gdx.app.postRunnable(() -> {
                        Unit unit = new Unit(finalMinionX, finalMinionY, finalMinionZ, tempMinion1Health, playerID);
                        state.addUnit(unit);
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
                "x=" + x +
                ", y=" + y +
                ", playerID=" + playerID +
                ", towerType=" + towerType +
                ", hp=" + hp +
                ", maxHp=" + maxHp +
                ", dmg=" + dmg +
                ", range=" + range +
                ", cost=" + cost +
                '}';
    }
    
    @Override
    public void getDisplayPos(Vector3 displayPos) {
        displayPos.set(highestBlockLocation);
    }
}
