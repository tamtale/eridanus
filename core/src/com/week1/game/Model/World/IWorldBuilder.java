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

    void addSeed(long mapSeed);
}
