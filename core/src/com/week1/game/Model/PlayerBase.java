package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.week1.game.Networking.Player;

import static com.week1.game.Renderer.Renderer.SCALE;

public class PlayerBase {
    public float x, y;
    private float hp;
    private int playerID;
    private Texture skin;
    private final int SIDELENGTH = 32;

    public PlayerBase(float initialHp, float x, float y, int playerID) {

        Pixmap towerUnscaled = new Pixmap(Gdx.files.internal("basetop.png"));
        Pixmap towerScaled = new Pixmap(SIDELENGTH, SIDELENGTH, Pixmap.Format.RGBA8888);
        towerScaled.drawPixmap(towerUnscaled, 0, 0, towerUnscaled.getWidth(), towerUnscaled.getHeight(), 0, 0, SIDELENGTH, SIDELENGTH);

        this.skin = new Texture(towerScaled);
        this.hp = initialHp;
        this.playerID = playerID;
        this.x = x;
        this.y = y;
    }

    public Texture getSkin() {
        return skin;
    }
}
