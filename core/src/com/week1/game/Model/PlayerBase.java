package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import static com.week1.game.Model.HealthBar.getHealthBar;
import static com.week1.game.Model.HealthBar.healthBarBackground;


public class PlayerBase implements Damageable {
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
        batch.draw(healthBarBackground, this.x - (SIDELENGTH / 2f), this.y + 9f - (SIDELENGTH / 2f), 8, .5f);
        batch.draw(getHealthBar(hp, maxHp), this.x - (SIDELENGTH / 2f), this.y + 9f - (SIDELENGTH / 2f), (float) (hp / maxHp) * 8, .5f);
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
}
