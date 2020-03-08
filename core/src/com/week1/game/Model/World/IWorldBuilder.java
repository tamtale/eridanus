package com.week1.game.Model.World;

import com.badlogic.gdx.math.Vector3;

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
    Vector3[] crystalLocations();

    void addSeed(long mapSeed);
}
