package com.week1.game.Model.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.week1.game.Model.Damage;

import java.util.HashMap;
import java.util.Map;

import static com.week1.game.Model.StatsConfig.placementRange;


public class PlayerBase implements Damageable {
    public float x, y;
    private double hp, maxHp;
    private int playerID;
    private static Texture skin;
    protected static final int SIDELENGTH = 8;
    private final static Map<Integer, Texture> colorMap = new HashMap<>();

    static {
        Pixmap towerUnscaled = new Pixmap(Gdx.files.internal("basetop.png"));
        Pixmap towerScaled = new Pixmap(SIDELENGTH, SIDELENGTH, Pixmap.Format.RGBA8888);
        towerScaled.drawPixmap(towerUnscaled, 0, 0, towerUnscaled.getWidth(), towerUnscaled.getHeight(), 0, 0, SIDELENGTH, SIDELENGTH);
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
        circlePixmap.dispose();
    }

    public PlayerBase(double initialHp, float x, float y, int playerID) {
        this.hp = initialHp;
        this.maxHp = initialHp;
        this.playerID = playerID;
        this.x = x;
        this.y = y;
    }

    public void draw(Batch batch, boolean showSpawnRadius) {
        if (showSpawnRadius) {
            batch.draw(colorMap.get(playerID), x - (float)placementRange, y - (float)placementRange, (float)placementRange * 2, (float)placementRange * 2);
        }
        batch.draw(getSkin(), this.x - (SIDELENGTH / 2f), this.y - (SIDELENGTH / 2f), SIDELENGTH, SIDELENGTH);
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
}
