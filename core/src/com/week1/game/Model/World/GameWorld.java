package com.week1.game.Model.World;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector3;

import static com.week1.game.GameScreen.PIXELS_PER_UNIT;

public class GameWorld {
    private Block[][][] blocks;
    private int[][] heightMap;
    private boolean refreshHeight = true; // whether or not the map has changed, warranting a new height map.
    private GameGraph graph;
    public GameWorld(IWorldBuilder worldBuilder) {
        // For now, we'll make a preset 100x100x10 world.
        blocks = worldBuilder.terrain();
        this.graph = new GameGraph(blocks);
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                graph.addVector3(new Vector3(i, j, 0));
//                if (i > 0) {
//                    blocks[i][j][0].setConnection(new WeightedBlockEdge(1, blocks[i][j][0], blocks[i - 1][j][0]));
//                }
                for (int k = 1; k < blocks[0][0].length; k++) {
                    graph.addVector3(new Vector3(i, j, k));
                }
            }
        }
        Gdx.app.log("Game World - wab2", "Block array built");
    }


    public Block getBlock(int i, int j, int k) {
        return blocks[i][j][k];
    }
    public void setBlock(int i, int j, int k, Block block) {
        blocks[i][j][k] = block;
        refreshHeight = true;
    }

    public TiledMap toTiledMap() {
        TiledMap map = new TiledMap();
        MapLayers layers = map.getLayers();
        TiledMapTileLayer layer = new TiledMapTileLayer(blocks.length, blocks[0].length, PIXELS_PER_UNIT, PIXELS_PER_UNIT);
        int[][] heightMap = getHeightMap();
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new StaticTiledMapTile(blocks[i][j][heightMap[i][j]].getTextureRegion()));
                layer.setCell(i, j, cell);
            }
        }
        layers.add(layer);
        return map;
    }


    public GameGraph buildGraph(){

        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
//                for (int k = 0; k < blocks[0][0].length; k++) {
                    Vector3 coords = new Vector3(i, j, 0);
                    if (i > 0 && Math.abs(heightMap[i][j] - heightMap[i - 1][j]) <= 1) {
                        graph.setConnection(coords, new Vector3(i - 1, j, 0), blocks[i][j][heightMap[i][j]].getCost());
                    }
                    if(i < blocks.length - 1) {
                        System.out.println(heightMap[i][j]);
                        System.out.println(heightMap[i + 1][j]);
                        if (Math.abs(heightMap[i][j] - heightMap[i + 1][j]) <= 1) {
                            graph.setConnection(coords, new Vector3(i + 1, j, 0), blocks[i][j][heightMap[i][j]].getCost());
                        }
                    }
                    if (j > 0 && Math.abs(heightMap[i][j] - heightMap[i][j - 1]) <= 1) {
                        graph.setConnection(coords, new Vector3(i, j - 1, 0), blocks[i][j][heightMap[i][j]].getCost());
                    }
                    if(j < blocks[0].length - 1 && Math.abs(heightMap[i][j] - heightMap[i][j + 1]) <= 1) {

                        graph.setConnection(coords, new Vector3(i, j + 1, 0), blocks[i][j][heightMap[i][j]].getCost());
                    }
                    //TODO: climbing jumping into k.
                    if (i > 0 && j > 0
                            && Math.abs(heightMap[i][j] - heightMap[i - 1][j - 1]) <= 1) {
                        graph.setConnection(coords, new Vector3(i - 1, j - 1, 0),
                                blocks[i][j][heightMap[i][j]].getCost() * (float) Math.sqrt(2));
                    }
                    if (i > 0 && j < blocks[0].length - 1
                            && Math.abs(heightMap[i][j] - heightMap[i - 1][j + 1]) <= 1) {
                        graph.setConnection(coords, new Vector3(i - 1, j + 1, 0),
                                blocks[i][j][heightMap[i][j]].getCost() * (float) Math.sqrt(2));
                    }
                    if (i < blocks.length - 1 && j > 0
                            && Math.abs(heightMap[i][j] - heightMap[i + 1][j - 1]) <= 1) {
                        graph.setConnection(coords, new Vector3(i + 1, j - 1, 0),
                                blocks[i][j][heightMap[i][j]].getCost() * (float) Math.sqrt(2));
                    }
                    if (i < blocks.length  - 1 && j < blocks[0].length - 1
                            && Math.abs(heightMap[i][j] - heightMap[i + 1][j + 1]) <= 1) {
                        graph.setConnection(coords, new Vector3(i + 1, j + 1, 0),
                                blocks[i][j][heightMap[i][j]].getCost() * (float) Math.sqrt(2));
                    }
//                }
            }
        }
        return graph;
    }

    /**
     * Lazily construct and returns the heightmap.
     * The height of (x, y) will just be the z-coordinate of the highest non-air block.
     */
    public int[][] getHeightMap() {
        if (refreshHeight) {
            heightMap = new int[blocks.length][blocks[0].length];
            for (int i = 0; i < blocks.length; i++) {
                for (int j = 0; j < blocks[0].length; j++) {
                    heightMap[i][j] = 0;
                    for (int k = 1; k < blocks[0][0].length; k++) {
                        if (blocks[i][j][k] != Block.TerrainBlock.AIR) {
                            heightMap[i][j] = k;
                        }
                    }
                }
            }
            refreshHeight = false;
        }

        return heightMap;
    }
}
