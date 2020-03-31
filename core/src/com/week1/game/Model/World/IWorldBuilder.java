package com.week1.game.Model.World;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.Pair;

import java.util.List;

public interface IWorldBuilder {
    /**
     * 3D voxel representation of the terrain of the world.
     */
    Block[][][] terrain();

    /**
     * Viable starting locations for player bases.
     */
    Vector3[] startLocations();

    /**
     * Locations of crystals on the map.
     */
    Vector2[] crystalLocations();

//    /**
//     * Gives the x and y coords of the next crystal spawn (z must be computed from the GameWorld)
//     */
//    default Vector2 getNextCrystalSpawn() {
//        return null;
//    }

    void addSeed(long mapSeed);
}
