package com.week1.game.Model.World;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.week1.game.Model.Entities.Unit;

public class GameWorld implements RenderableProvider {
    private Block[][][] blocks;
    private int[][] heightMap;
    private boolean refreshHeight = true; // whether or not the map has changed, warranting a new height map.
    private GameGraph graph;
    private Array<ModelInstance> instances = new Array<>();
    private Model model;
    private ModelBuilder modelBuilder = new ModelBuilder();
    AssetManager assets;

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
        model = modelBuilder.createBox(5f, 5f, 5f,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        // Build the modelinstances!
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                for (int k = 0; k < blocks[0][0].length; k++) {
                    blocks[i][j][k].modelInstance(i, j, k).ifPresent(modelInstance -> instances.add(modelInstance));
                }
            }
        }
    }


    public Block getBlock(int i, int j, int k) {
        return blocks[i][j][k];
    }
    public void setBlock(int i, int j, int k, Block block) {
        blocks[i][j][k] = block;

        System.out.println("block: " + blocks[i][j][k]);
        System.out.println("optional: " + blocks[i][j][k].modelInstance(i,j,k));
        blocks[i][j][k]
                .modelInstance(i,j,k)
                .ifPresent(modelInstance -> instances.add(modelInstance));
        refreshHeight = true;
    }

    public GameGraph buildGraph(){

        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                for (int k = 0; k < blocks[0][0].length; k++) {
                    Vector3 coords = new Vector3(i, j, k);
                    if (i > 0) {
                        graph.setConnection(coords, new Vector3(i - 1, j, k), blocks[i][j][k].getCost());
                    }
                    if(i < blocks.length - 1) {
                        graph.setConnection(coords, new Vector3(i + 1, j, k), blocks[i][j][k].getCost());
                    }
                    if (j > 0) {
                        graph.setConnection(coords, new Vector3(i, j - 1, k), blocks[i][j][k].getCost());
                    }
                    if(j < blocks[0].length - 1) {

                        graph.setConnection(coords, new Vector3(i, j + 1, k), blocks[i][j][k].getCost());
                    }
                    //TODO: climbing jumping into k.
//                    if (k > 0) {
//                        graph.setConnection(blocks[i][j][k].getCost(), blocks[i][j][k], blocks[i][j][k - 1]);
//                    }
//                    if(k < blocks[0].length) {
//                        graph.setConnection(blocks[i][j][k].getCost(), blocks[i][j][k], blocks[i][j][k + 1]);
                    if (i > 0 && j > 0) {
                        graph.setConnection(coords, new Vector3(i - 1, j - 1, k),
                                blocks[i][j][k].getCost() * (float) Math.sqrt(2));
                    }
                    if (i > 0 && j < blocks[0].length - 1) {
                        graph.setConnection(coords, new Vector3(i - 1, j + 1, k),
                                blocks[i][j][k].getCost() * (float) Math.sqrt(2));
                    }
                    if (i < blocks.length - 1 && j > 0) {
                        graph.setConnection(coords, new Vector3(i + 1, j - 1, k), blocks[i][j][k].getCost() * (float) Math.sqrt(2));
                    }
                    if (i < blocks.length  - 1 && j < blocks[0].length - 1) {
                        graph.setConnection(coords, new Vector3(i + 1, j + 1, k), blocks[i][j][k].getCost() * (float) Math.sqrt(2));
                    }
                }
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
        }

        return heightMap;
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        instances.forEach(instance -> instance.getRenderables(renderables, pool));
    }
}
