package com.week1.game.MenuScreens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import static com.week1.game.Renderer.TextureUtils.makeTexture;

public class Resources {
    public static BitmapFont gameFont = new BitmapFont();
    public static Texture stringTexture = makeTexture(3, 3, Color.CYAN);
}
