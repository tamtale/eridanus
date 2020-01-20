package com.week1.game.Model;

import com.badlogic.gdx.math.Vector3;

public interface IClickOracle2EngineAdapter {
    Unit selectUnit(Vector3 position);
    Unit spawn(Vector3 position);
}
