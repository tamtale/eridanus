package com.week1.game.Model;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;

public interface IEngineToRendererAdapter {
    void sendToModelBatch(RenderableProvider provider);
    void setDefaultLocation(Vector3 location);

    void endGame(int winOrLoss); // loss is 0, win is 1

    void gameOver();
}
