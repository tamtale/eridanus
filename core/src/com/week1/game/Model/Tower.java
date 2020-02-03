package com.week1.game.Model;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import static com.week1.game.Model.HealthBar.getHealthBar;
import static com.week1.game.Model.HealthBar.healthBarBackground;

public class Tower implements Damageable, Damaging {
    private static final int SIDELENGTH = 3;
    public float x, y;
    private Texture skin;
    private int playerID;

    private double hp, maxHp, dmg, range, cost;
    
    private Damage.type attackType;


    public Tower(float x, float y, double hp, double dmg, double range, Damage.type attackType, double cost, Pixmap towerUnscaled, int playerID) {
        this.x = x;
        this.y = y;
        this.hp = hp;
        this.maxHp = hp;
        this.dmg = dmg;
        this.cost = cost;
        this.range = range;
        this.playerID = playerID;
        this.attackType = attackType;

        Pixmap towerScaled = new Pixmap(SIDELENGTH, SIDELENGTH, towerUnscaled.getFormat());
        towerScaled.drawPixmap(towerUnscaled, 0, 0, towerUnscaled.getWidth(), towerUnscaled.getHeight(),
                0, 0, SIDELENGTH, SIDELENGTH);
        this.skin = new Texture(towerScaled);
    }
    
    public void draw(Batch batch) {
        batch.draw(getSkin(), this.x, this.y, SIDELENGTH, SIDELENGTH);
        // TODO draw this in a UI rendering procedure
        batch.draw(healthBarBackground, this.x, (float) (this.y + 4), 3, .5f);
        batch.draw(getHealthBar(hp, maxHp), this.x, (float) (this.y + 4), (float) (hp / maxHp) * 3, .5f);
    }

    public Texture getSkin() {
        return skin;
    }

    public double getCost() {
        return cost;
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
}
