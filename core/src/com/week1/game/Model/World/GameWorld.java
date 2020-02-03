package com.week1.game.Model.World;


import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector3;

import static com.week1.game.GameScreen.PIXELS_PER_UNIT;

public class GameWorld {
    private Block[][][] blocks;
    private GameGraph graph;
    public GameWorld() {
        // For now, we'll make a preset 100x100x10 world.
        blocks = new Block[100][100][10];
        GameGraph graph = new GameGraph();
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                blocks[i][j][0] = new Block.TerrainBlock.StoneBlock(new Vector3(i, j, 0));
                graph.addBlock(blocks[i][j][0]);
//                if (i > 0) {
//                    blocks[i][j][0].setConnection(new WeightedBlockEdge(1, blocks[i][j][0], blocks[i - 1][j][0]));
//                }
                for (int k = 1; k < blocks[0][0].length; k++) {
                    blocks[i][j][k] = new Block.TerrainBlock.AirBlock(new Vector3(i, j, k));
                    graph.addBlock(blocks[i][j][k]);
                }
            }
        }
    }
    public Block getBlock(int i, int j, int k) {
        return blocks[i][j][k];
    }
    public void setBlock(int i, int j, int k, Block block) {
        blocks[i][j][k] = block;
    }

    public TiledMap toTiledMap() {
        TiledMap map = new TiledMap();
        MapLayers layers = map.getLayers();
        TiledMapTileLayer layer = new TiledMapTileLayer(blocks.length, blocks[0].length, PIXELS_PER_UNIT, PIXELS_PER_UNIT);
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new StaticTiledMapTile(blocks[i][j][0].getTextureRegion()));
                layer.setCell(i, j, cell);
            }
        }
        layers.add(layer);
        return map;
    }


    public GameGraph buildGraph(){

        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                for (int k = 0; k < blocks[0][0].length; k++) {
                    if (i > 0) {
                        graph.setConnection(blocks[i][j][k].getCost(), blocks[i][j][k], blocks[i - 1][j][k]);
                    }
                    if(i < blocks.length) {
                        graph.setConnection(blocks[i][j][k].getCost(), blocks[i][j][k], blocks[i + 1][j][k]);
                    }
                    if (j > 0) {
                        graph.setConnection(blocks[i][j][k].getCost(), blocks[i][j][k], blocks[i][j - 1][k]);
                    }
                    if(j < blocks[0].length) {
                        graph.setConnection(blocks[i][j][k].getCost(), blocks[i][j][k], blocks[i][j + 1][k]);
                    }
                    //TODO: climbing jumping into k.
//                    if (k > 0) {
//                        graph.setConnection(blocks[i][j][k].getCost(), blocks[i][j][k], blocks[i][j][k - 1]);
//                    }
//                    if(k < blocks[0].length) {
//                        graph.setConnection(blocks[i][j][k].getCost(), blocks[i][j][k], blocks[i][j][k + 1]);
                    if (i > 0 && j > 0) {
                        graph.setConnection(blocks[i][j][k].getCost() * (float) Math.sqrt(2),
                                blocks[i][j][k], blocks[i - 1][j - 1][k]);
                    }
                    if (i > 0 && j < blocks[0].length) {
                        graph.setConnection(blocks[i][j][k].getCost() * (float) Math.sqrt(2),
                                blocks[i][j][k], blocks[i - 1][j + 1][k]);
                    }
                    if (i < blocks.length && j > 0) {
                        graph.setConnection(blocks[i][j][k].getCost() * (float) Math.sqrt(2),
                                blocks[i][j][k], blocks[i + 1][j - 1][k]);
                    }
                    if (i < blocks.length && j < blocks[0].length) {
                        graph.setConnection(blocks[i][j][k].getCost() * (float) Math.sqrt(2),
                                blocks[i][j][k], blocks[i + 1][j + 1][k]);
                    }
                }
            }
        }
        return graph;
    }
}
