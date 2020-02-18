package com.week1.game.Model.World;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector3;

/**
 * World builder for a basic 4-player game.
 * 200x200, with plateaus in the middle.
 */
public class Basic4WorldBuilder implements IWorldBuilder {

    public static Basic4WorldBuilder ONLY = new Basic4WorldBuilder();

    @Override
    public Block[][][] terrain() {
        // empty block
        Block[][][] blocks = new Block[200][200][4];
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                blocks[i][j][0] = Block.TerrainBlock.STONE;
                for (int k = 1; k < blocks[0][0].length; k++) {
                    blocks[i][j][k] = Block.TerrainBlock.AIR;
                }
            }
        }
        makePlateau(blocks, 50, 70, 50, 70);
        makePlateau(blocks, 50, 70, 130, 150);
        makePlateau(blocks, 130, 150, 50, 70);
        makePlateau(blocks, 130, 150, 130, 150);
        return blocks;
    }

    private void makePlateau(Block[][][] blocks, int startX, int endX, int startY, int endY) {
        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {
                blocks[i][j][1] = Block.TerrainBlock.DIRT;
            }
        }
    }

    @Override
    public Vector3[] startLocations() {
        return new Vector3[] {
                new Vector3(25, 25, 0),
                new Vector3(25, 175, 0),
                new Vector3(175, 25, 0),
                new Vector3(175, 175, 0)
        };
    }
}