package com.week1.game.Model.Entities;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.Damage;
import com.week1.game.TowerBuilder.BlockSpec;
import com.week1.game.TowerBuilder.TowerDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.week1.game.Model.StatsConfig.*;

public class Tower extends Building implements Damaging {
    private static final int SIDELENGTH = 3;
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

        this.layout = towerDetails.getLayout();
    }


    public double getCost() {
        return cost;
    }
    
    public int getTowerType() {
        return towerType;
    }

    public double getHp() { return hp; }

    @Override
    public boolean takeDamage(double dmg, Damage.type damageType) {
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
}
