package com.week1.game.Model;

import com.badlogic.gdx.graphics.Texture;

public interface IEngineToRendererAdapter {

    void batchGame(Runnable r);
    void draw(Texture t, float x, float y);
    void endGame(int winOrLoss); // loss is 0, win is 1
}
