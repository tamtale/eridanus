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
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.week1.game.Model.Entities.Clickable;
import com.week1.game.Model.Entities.Unit;

public class GameWorld implements RenderableProvider {
    private Block[][][] blocks;
    private int[][] heightMap;
    private boolean refreshHeight = true; // whether or not the map has changed, warranting a new height map.
    private GameGraph graph;
//    private Array<ModelInstance> instances = new Array<>();
    private ModelInstance[][][] instances;
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
        
        instances = new ModelInstance[blocks.length][blocks[0].length][blocks[0][0].length];
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                for (int k = 0; k < blocks[0][0].length; k++) {
                    int i_final = i;
                    int j_final = j;
                    int k_final = k;
                    blocks[i][j][k].modelInstance(i, j, k)
                            .ifPresent(modelInstance -> instances[i_final][j_final][k_final] = modelInstance);
                }
            }
        }
    }


    public Block getBlock(int i, int j, int k) {
        return blocks[i][j][k];
    }
    public void setBlock(int i, int j, int k, Block block) {
        blocks[i][j][k] = block;
        blocks[i][j][k]
                .modelInstance(i,j,k)
                .ifPresent(modelInstance -> instances[i][j][k] = modelInstance);
        
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
        for (int i = 0; i < instances.length; i++) {
            for (int j = 0; j < instances[0].length; j++) {
                for (int k = 0; k < instances[0][0].length; k++) {
                    if (instances[i][j][k] != null) {
                        instances[i][j][k].getRenderables(renderables, pool);
                    }
                }
            }
        }
    }
    
    /*
        Returns the closest block to the camera that intersects with the given ray.
     */
    public Clickable getBlockOnRay(Ray ray, Vector3 intersection) {
        
        // TODO: doesn't actually return the closest one right now, just the first one it finds
        
//        BoundingBox box = new BoundingBox();
        
        Vector3 intermediateIntersection = new Vector3();
        BoundingBox intermediateBox = new BoundingBox();
        Vector3 closestIntersection = new Vector3();
        BoundingBox closestBox = new BoundingBox();
        ModelInstance closestModelInstance = null;
        float minDistance = Float.MAX_VALUE;
        Vector3 closestCoords = new Vector3();
        
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++)  {
                for (int k = 0; k < blocks[0][0].length; k++) {
                    ModelInstance modelInstance = instances[i][j][k];
                    // Ignore the null model instances, which correspond to empty spaces in the map
                    if (modelInstance == null) {
                        continue;
                    }
                    
                    // Calculate the bounding box on the fly
                    modelInstance.calculateBoundingBox(intermediateBox);
                    intermediateBox.mul(modelInstance.transform);
                    
                    if (Intersector.intersectRayBounds(ray, intermediateBox, intermediateIntersection)) {
                        
                        // Check distance between the origin of the ray and the intermediate intersection
                        float intermediateDistance = ray.origin.dst(intermediateIntersection);
                        if (intermediateDistance < minDistance) {
                            closestIntersection.set(intermediateIntersection);
                            closestBox.set(closestBox);
                            minDistance = intermediateDistance;
                            closestModelInstance = modelInstance;
                            closestCoords.set(i,j,k);
                        }
                    }
                }
            }
        }
        
        if (closestModelInstance == null) {
//            Gdx.app.log("GameState.getClickableOnRay", "No blocks clicked");
            return Clickable.NULL;
        }

//        Gdx.app.log("GameState.getClickableOnRay", "Returning clickable for block at i: " + closestCoords.x + " j: " + closestCoords.y + " k: " + closestCoords.z);
        ModelInstance closestModelInstance_final = closestModelInstance;
        return new Clickable() {
            private BoundingBox boundingBox = new BoundingBox(closestBox);
            private Material originalMaterial = closestModelInstance_final.model.materials.get(0);

            @Override
            public boolean intersects(Ray ray, Vector3 intersection) {
                return Intersector.intersectRayBounds(ray, boundingBox, intersection);
            }

            @Override
            public void setSelected(boolean selected) {
                Material mat = closestModelInstance_final.materials.get(0);
                mat.clear();
                if (selected) {
                    mat.set(Unit.selectedMaterial);
                } else {
                    mat.set(originalMaterial);
                }

            }

            @Override
            public <T> T accept(ClickableVisitor<T> clickableVisitor) {
                return clickableVisitor.acceptBlockLocation(closestCoords);
            }
        };
        

    }
}
