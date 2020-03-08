package com.week1.game.Model.World;

import com.badlogic.gdx.math.Vector3;

import java.util.Random;

public class CoolWorldBuilder implements IWorldBuilder {

    Block[][][] blocks;
    public static CoolWorldBuilder ONLY = new CoolWorldBuilder();
    private long seed;
    private Random random;

    @Override
    public Block[][][] terrain() {
        // empty block
        blocks = new Block[90][90][15];
        this.random = new Random(seed);
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                if (blocks[i][j][0] == null) {

                    float blockType = random.nextFloat();
                    if (blockType < 0.75) {
                        blocks[i][j][0] = Block.TerrainBlock.ZAMBALA;
                        spread(i, j, 0, .125f, .125f, .75f, true, Block.TerrainBlock.ZAMBALA);
                        //heightBuild(i, j, Block.TerrainBlock.ZAMBALA);
                    } else if (blockType < .98) {
                        blocks[i][j][0] = Block.TerrainBlock.FIREBRICK;
                        spread(i, j, 0, .125f, .125f, .85f, true, Block.TerrainBlock.FIREBRICK);
                        //heightBuild(i, j, Block.TerrainBlock.FIREBRICK);
                    } else {
                        blocks[i][j][0] = Block.TerrainBlock.WATER;
                        spread(i, j, 0,.23f, .02f, .98f, false, Block.TerrainBlock.WATER);

//                            } else {
//                                lake = 1;
//                            }
                        }
                    }
                for (int k = 1; k < blocks[0][0].length; k++) {
                    blocks[i][j][k] = Block.TerrainBlock.AIR;
                }

            }
        }
//        makePlateau(blocks, 0, 5, 0, 5);

//        blocks[3][3][2] = Block.TowerBlock.REDBLOCK;

        return blocks;
    }

    private void spread(int i, int j, int k, float ortho, float diag, float sticky, boolean height, Block.TerrainBlock block) {
        int m = i;
        int n = j;
        while (random.nextFloat() < sticky) {
            if (height){
//                int lim = blocks[0][0].length - 1;
                float incr = .975f - .01f * k;
                if (random.nextFloat() > incr && k < 2) {
                    k++;
                    diag = 0;
                    ortho = .25f;
                    sticky *= 1.7f;
                    sticky = Math.min(sticky, .99f);
                }
                if (random.nextFloat() < .02 && k > 0) {
                    k--;
                }
            }
            float dir = random.nextFloat();
            if ((m > 0 && m < blocks.length - 1) && (n > 0 && n < blocks[0].length - 1)) {
                if (dir < ortho) {
                    blocks[m][n - 1][k] = block;
                    n--;
                } else if (dir < ortho + diag) {
                    blocks[m - 1][n - 1][k] = block;
                    m--;
                    n--;
                } else if (dir < 2 * ortho + diag) {
                    blocks[m - 1][n][k] = block;
                    m--;
                } else if (dir < 2 * ortho + 2 * diag) {
                    if (n < blocks[0].length - 1) {
                        blocks[m - 1][n + 1][k] = block;
                        m--;
                        n++;
                    }
                } else if (dir < 3 * ortho + 2 * diag) {
                    blocks[m][n + 1][k] = block;
                    n++;
                } else if (dir < 3 * ortho + 3 * diag) {
                    blocks[m + 1][n + 1][k] = block;
                    m++;
                    n++;
                } else if (dir < 4 * ortho + 3 * diag) {
                    blocks[m + 1][n][k] = block;
                    m++;
                } else {
                    blocks[m + 1][n - 1][k] = block;
                    m++;
                    n--;
                }
                int o = k;
                while(o > 0) {
                    o--;
                    blocks[m][n][o] = block;
                }
            }

//                            } else {
//                                lake = 1;
//                            }
        }
    }
    private void heightBuild(int i, int j, Block.TerrainBlock block) {
        float plateau = random.nextFloat();
        if (plateau < .98) {
            blocks[i][j][0] = block;
        } else {
            spread(i, j, 1, .25f, 0f, .95f, true, block);
        }
    }


    private void makePlateau(Block[][][] blocks, int startX, int endX, int startY, int endY) {
        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {
                blocks[i][j][1] = Block.TerrainBlock.FIREBRICK;
            }
        }
    }

    @Override
    public Vector3[] startLocations() {
        return new Vector3[] {
                new Vector3(10, 10, 1),
                new Vector3(20, 20, 1),
                new Vector3(80, 10, 1),
                new Vector3(10, 80, 1)
        };
    }

    @Override
    public Vector3[] crystalLocations() {
        return new Vector3[0];
    }

    @Override
    public void addSeed(long mapSeed) {
        this.seed = mapSeed;
    }
}
