package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import static com.week1.game.Renderer.TextureUtils.makeTexture;

public class DestroyedBase extends PlayerBase {
    private static Texture skin;

    static {
//        Pixmap towerUnscaled = new Pixmap(Gdx.files.internal("basetop.png"));
//        Pixmap towerScaled = new Pixmap(PlayerBase.SIDELENGTH, PlayerBase.SIDELENGTH, Pixmap.Format.RGBA8888);
//        towerScaled.drawPixmap(towerUnscaled, 0, 0, towerUnscaled.getWidth(), towerUnscaled.getHeight(), 0, 0, SIDELENGTH, SIDELENGTH);
//        skin = new Texture(towerScaled);
        skin = makeTexture(PlayerBase.SIDELENGTH, PlayerBase.SIDELENGTH, Color.BLACK);
    }

    public DestroyedBase(double initialHp, float x, float y, int playerID) {
        super(initialHp, x, y, playerID);
        this.setSkin(skin);
    }

}
