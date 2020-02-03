package com.week1.game.Model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.week1.game.AIMovement.SteeringAgent;

import java.util.HashMap;
import java.util.Map;

import static com.week1.game.Model.StatsConfig.tempDamage;
import static com.week1.game.Model.StatsConfig.tempMinionRange;

public class Unit extends Rectangle implements Damageable, Damaging {
    private final int playerID;
    private float hp;
    private float maxHp;

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public boolean clicked = false;
    public SteeringAgent agent;
    public int ID;


    private static Texture makeTexture(Color color) {
        Pixmap map = new Pixmap(64, 64, Pixmap.Format.RGB888);
        map.setColor(color);
        map.fill();
        Texture texture = new Texture(map);
        map.dispose();
        return texture;
    }

    private static Texture healthBarHigh = makeTexture(Color.GREEN);
    private static Texture healthBarMid = makeTexture(Color.ORANGE);
    private static Texture healthBarLow = makeTexture(Color.FIREBRICK);
    private static Texture healthBarBackground = makeTexture(Color.BLACK);

    private static Texture selectedSkin = makeTexture(Color.YELLOW);

    public void draw(Batch batch) {
        batch.draw(getSkin(), this.x, this.y, 1, 1);
        // TODO draw this in a UI rendering procedure
        batch.draw(healthBarBackground, this.x, (float) (this.y + 1.5), 1, .5f);
        batch.draw(getHealthBar(), this.x, (float) (this.y + 1.5), hp / maxHp, .5f);
    }

    private Texture getHealthBar() {
        float perc = hp / maxHp;
        if (perc > .5) return healthBarHigh;
        else if (perc > .2) return healthBarMid;
        else return healthBarLow;
    }

    private final static Map<Integer, Texture> colorMap = new HashMap<Integer, Texture>() {
        {
            put(0, makeTexture(Color.BLUE));
            put(1, makeTexture(Color.RED));
            put(2, makeTexture(Color.WHITE));
            put(3, makeTexture(Color.PURPLE));
            put(4, makeTexture(Color.PINK));
        }
    };

    private Texture unselectedSkin;

    public Unit(float x, float y, Texture t, float hp, int playerID) {
        super(x, y, 1, 1);
        this.unselectedSkin = colorMap.get(playerID);
        this.playerID = playerID;
        this.hp = hp;
        this.maxHp = hp;
    }

    void step(float delta) {
        if (agent != null) {
            agent.update(delta);
        }
    }

    SteeringAgent getAgent(){ return agent;}
    public Texture getSelectedSkin(){
        return selectedSkin;
    }

    private Texture getSkin() {
        return clicked ? selectedSkin : unselectedSkin;
    }
    
    @Override
    public boolean takeDamage(float dmg, int damageType) {
        this.hp -= dmg;
        if (this.hp <= 0) {
            return true;
            // TODO again, might need to do more than returning true.
        } else {
            return false;
        }
    }
    
    
    public boolean isDead() {
        return this.hp <= 0;
    }

    @Override
    public boolean hasUnitInRange(Unit victim) {
        return Math.sqrt(Math.pow(this.x - victim.x, 2) + Math.pow(this.y - victim.y, 2)) < tempMinionRange;
//        return true; // TODO
    }
    
    @Override
    public float getDamage() {
        return tempDamage;
    }

    @Override
    public int getPlayerId(){return playerID;}
}

