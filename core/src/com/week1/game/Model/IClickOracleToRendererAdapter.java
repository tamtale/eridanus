package com.week1.game.Model;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public interface IClickOracleToRendererAdapter {
    void unproject(Vector3 projected);
    void setTranslationDirection(Direction direction);
    Ray getRay(float screenX, float screenY);
    Camera getCamera();
}
