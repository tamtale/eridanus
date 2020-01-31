package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class Tower implements Damageable {
    private static final int SIDELENGTH = 4;
    public float x, y;
    private float hp;
    private Texture skin;
    private int playerID;
    private TowerType type;

    public Tower(float x, float y, TowerType towerType, float hp, int playerID) {
        this.x = x;
        this.y = y;
        this.type = towerType;
        this.hp = hp;
        this.playerID = playerID;

        Pixmap towerUnscaled = null; // TODO: load different textures for different tower types
        if (towerType == TowerType.BASIC) {
            towerUnscaled = new Pixmap(Gdx.files.internal("towertransparent.png"));
        } else if (towerType == TowerType.SNIPER) {
            towerUnscaled = new Pixmap(Gdx.files.internal("tower3.png"));
        } else if (towerType == TowerType.TANK) {
            towerUnscaled = new Pixmap(Gdx.files.internal("towertransparent.png")); // TODO: tank skin
        }
        Pixmap towerScaled = new Pixmap(SIDELENGTH, SIDELENGTH, Pixmap.Format.RGBA8888);
        towerScaled.drawPixmap(towerUnscaled, 0, 0, towerUnscaled.getWidth(), towerUnscaled.getHeight(),
                0, 0, SIDELENGTH, SIDELENGTH);
        this.skin = new Texture(towerScaled);
    }


    public Texture getSkin() {
        return skin;
    }

    @Override
    public boolean takeDamage(float dmg, int damageType) {
        this.hp -= dmg;
        if (this.hp <= 0) {
            return true;
            // TODO probably need to send something to engine more than just returning true
        } else {
            return false;
        }
    }

    public int getSidelength(){
        return SIDELENGTH;
    }
}
