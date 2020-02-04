package com.week1.game.Model;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public interface IClickOracleToRendererAdapter {
    void unproject(Vector3 projected);
    void zoom(int amount);
    void setTranslationDirection(Direction direction);
}
