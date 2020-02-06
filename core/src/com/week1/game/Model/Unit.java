package com.week1.game.Model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.week1.game.AIMovement.SteeringAgent;

import java.util.HashMap;
import java.util.Map;

import static com.week1.game.Model.HealthBar.getHealthBar;
import static com.week1.game.Model.HealthBar.healthBarBackground;
import static com.week1.game.Model.StatsConfig.tempDamage;
import static com.week1.game.Model.StatsConfig.tempMinionRange;
import static com.week1.game.Renderer.TextureUtils.makeTexture;

public class Unit extends Rectangle implements Damageable, Damaging {
    private final int playerID;
    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    private double hp;
    private double maxHp;
    public boolean clicked = false;
    public SteeringAgent agent;
    public int ID;
    public  static int SIZE = 1;
    
    private static Texture selectedSkin = makeTexture(SIZE, SIZE, Color.YELLOW);

    public Unit(float x, float y, Texture t, double hp, int playerID) {
        super(x, y, 1, 1); // TODO make the x and y the center points of it for getX() and getY() which is used in range calculations
        this.unselectedSkin = colorMap.get(playerID);
        this.playerID = playerID;
        this.hp = hp;
        this.maxHp = hp;
    }

    public void draw(Batch batch) {
        batch.draw(getSkin(), this.x - (SIZE / 2f), this.y - (SIZE / 2f), SIZE, SIZE);
        // TODO draw this in a UI rendering procedure
        batch.draw(healthBarBackground, this.x - (SIZE / 2f), this.y + 1.5f - (SIZE / 2f), 1, .5f);
        batch.draw(getHealthBar(hp, maxHp), this.x - (SIZE / 2f), this.y + 1.5f - (SIZE / 2f), (float) (hp / maxHp), .5f);
    }


    private final static Map<Integer, Texture> colorMap = new HashMap<Integer, Texture>() {
        {
            put(0, makeTexture(SIZE, SIZE, Color.BLUE));
            put(1, makeTexture(SIZE, SIZE, Color.RED));
            put(2, makeTexture(SIZE, SIZE, Color.WHITE));
            put(3, makeTexture(SIZE, SIZE, Color.PURPLE));
            put(4, makeTexture(SIZE, SIZE, Color.PINK));
        }
    };

    private Texture unselectedSkin;


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
    public boolean takeDamage(double dmg, Damage.type damageType) {
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
    public boolean hasTargetInRange(Damageable victim) {
        return Math.sqrt(Math.pow(this.x - victim.getX(), 2) + Math.pow(this.y - victim.getY(), 2)) < tempMinionRange;
//        return true; // TODO
    }
    
    @Override
    public double getDamage() {
        return tempDamage;
    }

    @Override
    public int getPlayerId(){return playerID;}
}

