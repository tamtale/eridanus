package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import static com.week1.game.Model.StatsConfig.*;

public class Tower implements Damageable, Damaging {
    private static final int SIDELENGTH = 3;
    public float x, y;
    private Texture skin;
    private int playerID;

    private double hp, dmg, range;
    private Damage.type attackType;


    public Tower(float x, float y, double hp, double dmg, double range, Damage.type attackType, Pixmap towerUnscaled, int playerID) {
        this.x = x;
        this.y = y;
        this.hp = hp;
        this.dmg = dmg;
        this.range = range;
        this.playerID = playerID;
        this.attackType = attackType;

        Pixmap towerScaled = new Pixmap(SIDELENGTH, SIDELENGTH, towerUnscaled.getFormat());
        towerScaled.drawPixmap(towerUnscaled, 0, 0, towerUnscaled.getWidth(), towerUnscaled.getHeight(),
                0, 0, SIDELENGTH, SIDELENGTH);
        this.skin = new Texture(towerScaled);
    }

    public Texture getSkin() {
        return skin;
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
    public boolean hasUnitInRange(Unit victim) {
        return Math.sqrt(Math.pow(this.x - victim.x, 2) + Math.pow(this.y - victim.y, 2)) < tempTowerRange;
//        return true; // TODO
    }

    @Override
    public double getDamage() {
        return tempTowerDamage;
    }

    @Override
    public int getPlayerId(){return playerID;}
    public int getSidelength(){
        return SIDELENGTH;
    }
}
