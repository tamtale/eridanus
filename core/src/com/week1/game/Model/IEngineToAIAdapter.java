package com.week1.game.Model;

import com.badlogic.gdx.math.Vector3;
import com.week1.game.AIMovement.SteeringAgent;

import java.util.UUID;

public interface IEngineToAIAdapter {
    void spawn(Unit unit, UUID id);
    void spawnTower();//Tower class
    void updateTarget(Unit unit, Vector3 newTarget, UUID id);
    void buildMap();//Map class
    Vector3 update(Unit unit, UUID id);
}
