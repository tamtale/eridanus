package com.week1.game.Model.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.week1.game.AIMovement.SteeringAgent;
import com.week1.game.Model.Damage;

import java.util.HashMap;
import java.util.Map;

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
    private static Texture rangeCircle;

    static {
        Pixmap circlePixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
        circlePixmap.setBlending(Pixmap.Blending.None);
        circlePixmap.setColor(1, 1, 1, .5f);
        circlePixmap.drawCircle(50, 50, 50);
        rangeCircle = new Texture(circlePixmap);
    }

    public Unit(float x, float y, Texture t, double hp, int playerID) {
        super(x, y, 1, 1); // TODO make the x and y the center points of it for getX() and getY() which is used in range calculations
        this.unselectedSkin = colorMap.get(playerID);
        this.playerID = playerID;
        this.hp = hp;
        this.maxHp = hp;
    }

    public void draw(Batch batch, boolean showAttackRadius) {
        if (showAttackRadius) {
            batch.draw(rangeCircle, this.x - ((float)tempMinionRange), this.y - ((float)tempMinionRange), (float)tempMinionRange * 2, (float)tempMinionRange * 2);
        }
        batch.draw(getSkin(), this.x - (SIZE / 2f), this.y - (SIZE / 2f), SIZE, SIZE);
        // TODO draw this in a UI rendering procedure
        drawHealthBar(batch, this.x, this.y, 0, SIZE, this.hp, this.maxHp);
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


    public void step(float delta) {
        if (agent != null) {
            agent.update(delta);
        }
    }

    public SteeringAgent getAgent(){ return agent;}
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
    
    @Override
    public boolean contains(float x, float y) {
        return (this.x - (SIZE / 2f) < x) && (x < this.x + (SIZE / 2f)) &&
                (this.y - (SIZE / 2f) < y) && (y < this.y + (SIZE / 2f));
                
    }
}

