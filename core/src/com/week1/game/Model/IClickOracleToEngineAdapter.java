package com.week1.game.Model;

import com.badlogic.gdx.math.Vector3;

public interface IClickOracleToEngineAdapter {
    Unit selectUnit(Vector3 position);
    boolean isPlayerAlive();
}
