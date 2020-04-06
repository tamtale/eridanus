package com.week1.game.Model.World;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * World builder for a basic 4-player game.
 * 200x200, with plateaus in the middle.
 */
public class Medium4WorldBuilder implements IWorldBuilder {

    public static Medium4WorldBuilder ONLY = new Medium4WorldBuilder();

    @Override
    public Block[][][] terrain() {
        // empty block
        Block[][][] blocks = new Block[100][100][4];
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                blocks[i][j][0] = Block.TerrainBlock.STONE;
                for (int k = 1; k < blocks[0][0].length; k++) {
                    blocks[i][j][k] = Block.TerrainBlock.AIR;
                }
            }
        }
        return blocks;
    }

    private void makePlateau(Block[][][] blocks, int startX, int endX, int startY, int endY, int height) {
        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {
                blocks[i][j][height] = Block.TerrainBlock.DIRT;
            }
        }
        blocks[startX - 1][endY][height - 1] = Block.TerrainBlock.DIRT;
    }

    @Override
    public Vector3[] startLocations() {
        return new Vector3[] {
                new Vector3(25, 25, 0),
        };
    }
    @Override
    public Vector2[] crystalLocations() {
        return new Vector2[] {};
    }

    @Override
    public void addSeed(long mapSeed) {
        
    }
}
