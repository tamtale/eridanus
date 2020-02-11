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
     * TODO make this more generic
     */
    Vector3[] crystalLocations();
}
