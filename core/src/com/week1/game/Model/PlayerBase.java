package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.week1.game.Networking.Player;


public class PlayerBase implements Damageable {
    public float x, y;
    private float hp;
    private int playerID;
    private static Texture skin;
    private static final int SIDELENGTH = 8;
    static {
        Pixmap towerUnscaled = new Pixmap(Gdx.files.internal("basetop.png"));
        Pixmap towerScaled = new Pixmap(SIDELENGTH, SIDELENGTH, Pixmap.Format.RGBA8888);
        towerScaled.drawPixmap(towerUnscaled, 0, 0, towerUnscaled.getWidth(), towerUnscaled.getHeight(), 0, 0, SIDELENGTH, SIDELENGTH);
        skin = new Texture(towerScaled);
    }

    public PlayerBase(float initialHp, float x, float y, int playerID) {
        this.hp = initialHp;
        this.playerID = playerID;
        this.x = x;
        this.y = y;
    }

    public Texture getSkin() {
        return skin;
    }

    @Override
    public boolean takeDamage(float dmg, int damageType) {
        this.hp -= dmg;
        if (this.hp <= 0) {
            // This base has been destroyed. Something probably needs to be done here besides just return
            return true;
        } else {
            return false;
        }
    }

    public int getSidelength(){
        return SIDELENGTH;
    }
}
