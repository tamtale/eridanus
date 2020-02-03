package com.week1.game.Model;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public interface IClickOracleToEngineAdapter {
    Unit selectUnit(Vector3 position);
    boolean isPlayerAlive();
    Array<Unit> getUnitsInBox(Vector3 cornerA, Vector3 cornerB);
}
