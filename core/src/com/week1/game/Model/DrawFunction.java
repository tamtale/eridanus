package com.week1.game.Model;

import com.badlogic.gdx.graphics.Texture;

@FunctionalInterface
public interface DrawFunction {
    public void draw(Texture t, float x, float y);
}
