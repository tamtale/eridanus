package com.week1.game.Model.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.Model.Damage;

import static com.week1.game.Model.Entities.HealthBar.getHealthBar;
import static com.week1.game.Model.Entities.HealthBar.healthBarBackground;


public class PlayerBase extends Building {
    public float x, y;
    private double hp, maxHp;
    private int playerID;
    private static Texture skin;
    protected static final int SIDELENGTH = 8;

    static {
        Pixmap towerUnscaled = new Pixmap(Gdx.files.internal("basetop.png"));
        Pixmap towerScaled = new Pixmap(SIDELENGTH, SIDELENGTH, Pixmap.Format.RGBA8888);
        towerScaled.drawPixmap(towerUnscaled, 0, 0, towerUnscaled.getWidth(), towerUnscaled.getHeight(), 0, 0, SIDELENGTH, SIDELENGTH);
        skin = new Texture(towerScaled);
    }

    public PlayerBase(double initialHp, float x, float y, int playerID) {
        this.hp = initialHp;
        this.maxHp = initialHp;
        this.playerID = playerID;
        this.x = x;
        this.y = y;

    }

    public void draw(Batch batch) {
        batch.draw(getSkin(), this.x - (SIDELENGTH / 2f), this.y - (SIDELENGTH / 2f), SIDELENGTH, SIDELENGTH);
        // TODO draw this in a UI rendering procedure
        drawHealthBar(batch, x, y, 0, SIDELENGTH, hp, maxHp);
    }
    
    Texture getSkin() { return skin; }

    @Override
    public boolean takeDamage(double dmg, Damage.type damageType) {
        this.hp -= dmg;
        if (this.hp <= 0) {
            // This base has been destroyed. Something probably needs to be done here besides just return
            return true;
        } else {
            return false;
        }
    }

    @Override
    public float getX() { return this.x; }

    @Override
    public float getY() {
        return this.y;
    }

    public void setSkin(Texture t) { this.skin = t; }

    @Override
    public boolean isDead() {
        return this.hp <= 0;
    }

    @Override
    public int getPlayerId() {
        return this.playerID;
    }

    public int getSidelength(){
        return SIDELENGTH;
    }

    public double getHp() { return hp; }

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
