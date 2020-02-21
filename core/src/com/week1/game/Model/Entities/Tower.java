package com.week1.game.Model.Entities;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.Model.Damage;

import java.util.HashMap;
import java.util.Map;

import static com.week1.game.Model.StatsConfig.*;

public class Tower extends Building implements Damaging {
    private static final int SIDELENGTH = 3;
    public float x, y;
    private static Texture skin; // TODO change this when we go to 3D to actually use the model of the tower.
    private int playerID, towerType;
    private final static Map<Integer, Texture> colorMap = new HashMap<>();
    private static Texture rangeCircle;
    private double hp, maxHp, dmg, range, cost;

    private Damage.type attackType;


    public Tower(float x, float y, double hp, double dmg, double range, Damage.type attackType, double cost, int playerID, int towerType) {
        this.x = x;
        this.y = y;
        this.hp = hp;
        this.maxHp = hp;
        this.dmg = dmg;
        this.cost = cost;
        this.range = range;
        this.playerID = playerID;
        this.attackType = attackType;
        this.towerType = towerType;
    }

    public static void makeTextures() {
        Pixmap towerScaled = new Pixmap(SIDELENGTH, SIDELENGTH, sniperTexture.getFormat());
        towerScaled.drawPixmap(sniperTexture, 0, 0, sniperTexture.getWidth(), sniperTexture.getHeight(),
                0, 0, SIDELENGTH, SIDELENGTH);
        skin = new Texture(towerScaled);

        // Make the textures for the circles surrounding the tower
        Pixmap circlePixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
        circlePixmap.setBlending(Pixmap.Blending.None);

        circlePixmap.setColor(0, 0, 1, .1f);
        circlePixmap.fillCircle(50, 50, 50);
        colorMap.put(0, new Texture(circlePixmap));

        circlePixmap.setColor(1, 0, 0, .1f);
        circlePixmap.fillCircle(50, 50, 50);
        colorMap.put(1, new Texture(circlePixmap));

        circlePixmap.setColor(0, 0, 0, .1f);
        circlePixmap.fillCircle(50, 50, 50);
        colorMap.put(2, new Texture(circlePixmap));

        circlePixmap.setColor(0.5f, 0, 0.5f, .1f);
        circlePixmap.fillCircle(50, 50, 50);
        colorMap.put(3, new Texture(circlePixmap));

        circlePixmap.setColor(0.6f, 0.05f, 0.35f, .1f);
        circlePixmap.fillCircle(50, 50, 50);
        colorMap.put(4, new Texture(circlePixmap));

        // Make the radius that towers can attack;
        circlePixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
        circlePixmap.setBlending(Pixmap.Blending.None);
        circlePixmap.setColor(1, 1, 1, .5f);
        circlePixmap.drawCircle(50, 50, 50);
        rangeCircle = new Texture(circlePixmap);
        circlePixmap.dispose();
    }
    
    public void draw(Batch batch, boolean showAttackRadius, boolean showSpawnRadius) {
        if (showSpawnRadius) {
            batch.draw(colorMap.get(playerID), x - (float)placementRange, y - (float)placementRange, (float)placementRange * 2, (float)placementRange * 2);
        }
        if (showAttackRadius) {
            batch.draw(rangeCircle, x - (float)range, y - (float)range, (float)range * 2, (float)range * 2);
        }
        batch.draw(getSkin(), this.x - (SIDELENGTH / 2f) + 0.5f, this.y - (SIDELENGTH / 2f) + 0.5f, SIDELENGTH, SIDELENGTH);
        // TODO draw this in a UI rendering procedure
        drawHealthBar(batch, x, y, 0.5f, SIDELENGTH, hp, maxHp);
    }

    public Texture getSkin() {
        return skin;
    }

    public double getCost() {
        return cost;
    }
    
    public int getTowerType() {
        return towerType;
    }

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
}
