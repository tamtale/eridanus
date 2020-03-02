package com.week1.game.Model.World;

import com.badlogic.gdx.math.Vector3;

/**
 * World builder for a basic 4-player game.
 * 200x200, with plateaus in the middle.
 */
public class SmallWorldBuilder implements IWorldBuilder {

    public static SmallWorldBuilder ONLY = new SmallWorldBuilder();

    @Override
    public Block[][][] terrain() {
        // empty block
        Block[][][] blocks = new Block[90][30][15];
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                blocks[i][j][0] = Block.TerrainBlock.STONE;
                for (int k = 1; k < blocks[0][0].length; k++) {
                    blocks[i][j][k] = Block.TerrainBlock.AIR;
                }
            }
        }
        makePlateau(blocks, 0, 5, 0, 5);
        
//        blocks[3][3][2] = Block.TowerBlock.REDBLOCK;
        
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
                new Vector3(10, 10, 1),
                new Vector3(75, 10, 1),
        };
    }

    @Override
    public Vector3[] crystalLocations() {
        return new Vector3[0];
    }
}
