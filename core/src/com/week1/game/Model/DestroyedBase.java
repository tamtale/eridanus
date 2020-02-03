package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import static com.week1.game.Renderer.TextureUtils.makeTexture;

public class DestroyedBase extends PlayerBase {
//    private static Texture destroyed_skin = makeTexture(PlayerBase.SIDELENGTH, PlayerBase.SIDELENGTH, Color.BLACK);

    public DestroyedBase(double initialHp, float x, float y, int playerID) {
        super(initialHp, x, y, playerID);
//        this.setSkin(destroyed_skin);
    }

}
