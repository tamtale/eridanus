package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class Tower {
    private static final int SIDELENGTH = 4;
    public float x, y;
    private Texture skin;
    private int playerID;
    private TowerType type;

    public Tower(float x, float y, TowerType towerType, int playerID) {
        this.x = x;
        this.y = y;
        this.type = towerType;
        this.playerID = playerID;

        Pixmap towerUnscaled = null; // TODO: load different textures for different tower types
        if (towerType == TowerType.BASIC) {
            towerUnscaled = new Pixmap(Gdx.files.internal("towertransparent.png")); // TODO: basic skin
        } else if (towerType == TowerType.SNIPER) {
            towerUnscaled = new Pixmap(Gdx.files.internal("tower3.png")); // TODO: sniper skin
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
}
