package com.week1.game.Model;

import com.badlogic.gdx.math.Vector3;

public interface IEngineToRendererAdapter {
    void setDefaultLocation(Vector3 location);
    void endGame(int winOrLoss); // loss is 0, win is 1
}
