package com.week1.game.Model.World;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.Pair;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.Color;

import java.util.*;

public class CoolWorldBuilder implements IWorldBuilder {

    Block[][][] blocks;
    public static CoolWorldBuilder ONLY = new CoolWorldBuilder();
    private long seed;
    private Random random;

    final static List<Pair<Material, Material>> blockColors = new ArrayList<Pair<Material, Material>> () {{
        this.add(new Pair<>(new Material(ColorAttribute.createDiffuse(Color.PURPLE)), new Material(ColorAttribute.createDiffuse(Color.PINK))));
        this.add(new Pair<>(new Material(ColorAttribute.createDiffuse(Color.FIREBRICK)), new Material(ColorAttribute.createDiffuse(Color.CORAL))));
        this.add(new Pair<>(new Material(ColorAttribute.createDiffuse(new Color(0x660000))), new Material(ColorAttribute.createDiffuse(Color.LIME))));
    }};
    
    @Override
    public Block[][][] terrain() {
        // empty block
        blocks = new Block[90][90][15];
        this.random = new Random(seed);
        
        Pair<Material, Material> materials = blockColors.get(random.nextInt(blockColors.size()));
        Block.TerrainBlock.FIREBRICK.model.materials.get(0).clear();
        Block.TerrainBlock.FIREBRICK.model.materials.get(0).set(materials.key);
        Block.TerrainBlock.ZAMBALA.model.materials.get(0).clear();
        Block.TerrainBlock.ZAMBALA.model.materials.get(0).set(materials.value);
        
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                if (blocks[i][j][0] == null) {

                    float blockType = random.nextFloat();
                    if (blockType < 0.75) {
                        blocks[i][j][0] = Block.TerrainBlock.FIREBRICK;
                        spread(i, j, 0, .125f, .125f, .75f, 0f,  true, Block.TerrainBlock.ZAMBALA); //zamb
                        //heightBuild(i, j, Block.TerrainBlock.ZAMBALA);
                    } else if (blockType < .98) {
                        blocks[i][j][0] = Block.TerrainBlock.ZAMBALA;
                        spread(i, j, 0, .125f, .125f, .85f, 0f, true, Block.TerrainBlock.FIREBRICK); //fire
                        //heightBuild(i, j, Block.TerrainBlock.FIREBRICK);
                    } else {
                        blocks[i][j][0] = Block.TerrainBlock.WATER;
                        float lake = random.nextFloat();
                        float flat;
                        if (lake < .9){
                            flat = .8f;
                        } else{
                            flat = -.07f;
                        }
                        spread(i, j, 0,.23f, .02f, .98f, flat, false, Block.TerrainBlock.WATER);

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

    private void spread(int i, int j, int k, float ortho, float diag, float sticky, float flat, boolean height, Block.TerrainBlock block) {
        int m = i;
        int n = j;
        float north = 0f;
        float east = 0f;
        while (random.nextFloat() < sticky) {
            if (height){
//                int lim = blocks[0][0].length - 1;
                float incr = .997f + .0015f * k;
                if (random.nextFloat() > incr && k < 2) {
                    k++;
                    diag = 0;
                    ortho = .25f;
                    sticky *= 2.7f;
                    sticky = Math.min(sticky, .99999f);
                    flat = .7f;
                }
                if (random.nextFloat() < .001 && k > 0) {
                    k--;
                }
            }
            float dir = random.nextFloat();
            if ((m > 0 && m < blocks.length - 1) && (n > 0 && n < blocks[0].length - 1)) {
                if (dir < ortho + north) {
                    blocks[m][n - 1][k] = block;
                    n--;
                    north -= flat;
                } else if (dir < ortho + north + diag) {
                    blocks[m - 1][n - 1][k] = block;
                    m--;
                    n--;
                } else if (dir < 2 * ortho + diag + north + east) {
                    blocks[m - 1][n][k] = block;
                    m--;
                    east -= flat;
                } else if (dir < 2 * ortho + 2 * diag + north + east) {
                    if (n < blocks[0].length - 1) {
                        blocks[m - 1][n + 1][k] = block;
                        m--;
                        n++;
                    }
                } else if (dir < 3 * ortho + 2 * diag - north + east) {
                    blocks[m][n + 1][k] = block;
                    north += flat;
                    n++;
                } else if (dir < 3 * ortho + 3 * diag + east) {
                    blocks[m + 1][n + 1][k] = block;
                    m++;
                    n++;
                } else if (dir < 4 * ortho + 3 * diag - east) {
                    blocks[m + 1][n][k] = block;
                    east += flat;
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
                Pair<Integer, Integer> mn = updateLoc(m,n, k, block);
                if (mn.key != m || mn.value != n){
                    sticky -=.0002f;
                }
                m = mn.key;
                n = mn.value;
            }

//                            } else {
//                                lake = 1;
//                            }
        }
    }

    private Pair<Integer, Integer> updateLoc(int m, int n, int o, Block.TerrainBlock block) {
        if ((m > 0 && m < blocks.length - 1) && (n > 0 && n < blocks[0].length - 1)) {
            if (blocks[m - 1][n][o] == block &&
                    blocks[m + 1][n][o] == block &&
                    blocks[m][n - 1][o] == block &&
                    blocks[m][n + 1][o] == block) {
                float dir = random.nextFloat();
                if (dir < .25) {
                    while (blocks[m - 1][n][o] == block) {
                        m--;
                        if (m < 1) {
                            break;
                        }
                    }
                } else if (dir < .5) {
                    while (blocks[m + 1][n][o] == block) {
                        m++;
                        if (m > blocks.length - 2) {
                            break;
                        }
                    }
                } else if (dir < .75) {
                    while (blocks[m][n - 1][o] == block) {
                        n--;
                        if (n < 1) {
                            break;
                        }
                    }
                } else {
                    while (blocks[m][n + 1][o] == block) {
                        n++;
                        if (n > blocks[0].length - 2) {
                            break;
                        }
                    }
                }
            }
        }
        return new Pair<>(m, n);
    }

//    private void heightBuild(int i, int j, Block.TerrainBlock block) {
//        float plateau = random.nextFloat();
//        if (plateau < .98) {
//            blocks[i][j][0] = block;
//        } else {
//            spread(i, j, 1, .25f, 0f, .95f, true, block);
//        }
//    }


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
