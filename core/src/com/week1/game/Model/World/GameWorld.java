package com.week1.game.Model.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.week1.game.Model.Entities.Clickable;
import com.week1.game.Model.Entities.Unit;
import com.week1.game.Pair;
import com.week1.game.Renderer.GameRenderable;
import com.week1.game.Renderer.RenderConfig;

public class GameWorld implements GameRenderable {
    private Block[][][] blocks;
    private int[][] heightMap;
    private boolean refreshHeight = true; // whether or not the map has changed, warranting a new height map.
    private GameGraph graph;
    private ModelInstance[][][] instances;
    private boolean updateCache = true; // Whether or not the cache needs to be updated.
    private ModelCache modelCache = new ModelCache();
    private BoundingBox[][][] boundingBoxes;
    
    private BoundingBox[][][] chunkBoundingBoxes;
    private int[][][] activeBlocksPerChunk;
    
    public static final float blockOffset = 0.5f;

//    private Model model;
    private ModelBuilder modelBuilder = new ModelBuilder();
    AssetManager assets;
    
    private int chunkSide;
    private int chunkHeight;

    public GameWorld(IWorldBuilder worldBuilder) {
        blocks = worldBuilder.terrain();
        this.graph = new GameGraph(blocks);
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                graph.addVector3(new Vector3(i, j, 0));
                for (int k = 1; k < blocks[0][0].length; k++) {
                    graph.addVector3(new Vector3(i, j, k));
                }
            }
        }
        Gdx.app.log("Game World - wab2", "Block array built");
        // Set up the chunk bounding boxes
        chunkSide = (int)Math.pow(blocks.length * blocks[0].length * blocks[0][0].length, 1d/4d);
        chunkHeight = 5;
        int sizeX = (int)Math.ceil(blocks.length / (double)chunkSide);
        int sizeY = (int)Math.ceil(blocks[0].length / (double)chunkSide);
        int sizeZ = (int)Math.ceil(blocks[0][0].length / (double)chunkHeight);
        chunkBoundingBoxes = new BoundingBox[sizeX][sizeY][sizeZ];
        activeBlocksPerChunk = new int[sizeX][sizeY][sizeZ];
        Vector3 minCorner = new Vector3();
        Vector3 maxCorner = new Vector3();

        for (int i = 0; i < chunkBoundingBoxes.length; i++) {
            for (int j = 0; j < chunkBoundingBoxes[0].length; j++) {
                for (int k = 0; k < chunkBoundingBoxes[0][0].length; k++) {
                    minCorner.set((i * chunkSide) - blockOffset, (j * chunkSide) - blockOffset, (k * chunkHeight) - blockOffset);
                    maxCorner.set(((i + 1) * chunkSide) - blockOffset, ((j + 1) * chunkSide) - blockOffset, ((k + 1) * chunkHeight) - blockOffset);
                    chunkBoundingBoxes[i][j][k] = new BoundingBox(minCorner, maxCorner);
                }
            }
        }
        
        // Build the modelinstances and precompute the bounding boxes
        instances = new ModelInstance[blocks.length][blocks[0].length][blocks[0][0].length];
        boundingBoxes = new BoundingBox[blocks.length][blocks[0].length][blocks[0][0].length];
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                for (int k = 0; k < blocks[0][0].length; k++) {
                    int i_final = i;
                    int j_final = j;
                    int k_final = k;
                    blocks[i][j][k].modelInstance(i, j, k)
                            .ifPresent(modelInstance -> instances[i_final][j_final][k_final] = modelInstance);
                    
                    boundingBoxes[i][j][k] = new BoundingBox();
                    updateBoundingBox(i,j,k);
                    updateActiveBlocks(i, j, k);
                }
            }
        }

    }

    private void updateActiveBlocks(int i, int j, int k) {
        
        int chunkX = i / chunkSide;
        int chunkY = j / chunkSide;
        int chunkZ = k / chunkHeight;
        
        
        if (instances[i][j][k] == null) {
            activeBlocksPerChunk[chunkX][chunkY][chunkZ]--;
        } else {
            activeBlocksPerChunk[chunkX][chunkY][chunkZ]++;
        }
        
    }
    
    private void updateBoundingBox(int i, int j, int k) {
        if (instances[i][j][k] != null) {
            instances[i][j][k].calculateBoundingBox(boundingBoxes[i][j][k]);
            boundingBoxes[i][j][k].mul(instances[i][j][k].transform);
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
        updateBoundingBox(i,j,k);
        updateActiveBlocks(i,j,k);
        refreshHeight = true;
        updateCache = true;
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


    public Pair<ModelInstance, Float> getBlockOnRayByChunk(
            Ray ray, 
            float minDistance, 
            Vector3 closestIntersection,
            BoundingBox closestBox, 
            ModelInstance closestModelInstance, 
            Vector3 closestCoords, 
            int minx, int miny, int minz, 
            int maxx, int maxy, int maxz) {
        // max is exclusive
        
        int nonNull = 0;

        Vector3 intermediateIntersection = new Vector3();

        for (int i = minx; i < maxx; i++) {
            for (int j = miny; j < maxy; j++) {
                for (int k = minz; k < maxz; k++) {
//                    System.out.println("(" + i + ", " + j + ", " + k + ")");
                    ModelInstance modelInstance = instances[i][j][k];
                    // Ignore the null model instances, which correspond to empty spaces in the map
                    if (modelInstance == null) {
                        continue;
                    }
                    nonNull++;

                    numIntersections++;
                    if (Intersector.intersectRayBounds(ray, boundingBoxes[i][j][k], intermediateIntersection)) {

                        // Check distance between the origin of the ray and the intermediate intersection
                        float intermediateDistance = ray.origin.dst(intermediateIntersection);
                        if (intermediateDistance < minDistance) {
                            closestIntersection.set(intermediateIntersection);
                            closestBox.set(boundingBoxes[i][j][k]);
                            minDistance = intermediateDistance;
                            closestModelInstance = modelInstance;
                            closestCoords.set(i, j, k);
                        }
                    }
                }
            }
        }

//        System.out.println("\tNonnull: " + nonNull);
        
            return new Pair<>(closestModelInstance, minDistance);
    }

    private int numIntersections;
    private int numChunkIntersections;
    /*
        Returns the closest block to the camera that intersects with the given ray.
     */
    public Clickable getBlockOnRay(Ray ray, Vector3 intersection) {
        numIntersections = 0;
        numChunkIntersections = 0;
        
        // If too slow again, could try maintaining a 'visible' group of blocks, which excludes blocks that are buried under others and can't be clicked
        
        // Search in chunks
//        Vector3 intermediateIntersection = new Vector3();
        Vector3 closestIntersection = new Vector3();
        BoundingBox closestBox = new BoundingBox();
        ModelInstance closestModelInstance = null;
        float minDistance = Float.MAX_VALUE;
        Vector3 closestCoords = new Vector3();
        
        
//        System.out.println("Chunks: ");
        Vector3 throwAway = new Vector3();
        for (int i = 0; i < chunkBoundingBoxes.length; i++) {
            for (int j = 0; j < chunkBoundingBoxes[0].length; j++) {
                for (int k = 0; k < chunkBoundingBoxes[0][0].length; k++) {
                    if (activeBlocksPerChunk[i][j][k] == 0) {
//                        System.out.println("jIgnoring chunk, because it contains no active blocks.");
                        continue;
                    }

                    numChunkIntersections++;
                    if (Intersector.intersectRayBounds(ray, chunkBoundingBoxes[i][j][k], throwAway)) {
//                        System.out.println("\t(" + i + ", " + j + ", " + k + ") - " + chunkBoundingBoxes[i][j][k].min + ", " + chunkBoundingBoxes[i][j][k].max + " - " + throwAway);
                        // now check that chunk and get the closest from that chunk
                        Pair<ModelInstance, Float> chunkResult = getBlockOnRayByChunk(ray, 
                                minDistance, closestIntersection, closestBox, closestModelInstance, closestCoords,
                                (int)(chunkBoundingBoxes[i][j][k].min.x + blockOffset),
                                (int)(chunkBoundingBoxes[i][j][k].min.y + blockOffset),
                                (int)(chunkBoundingBoxes[i][j][k].min.z + blockOffset),
                                Math.min((int)(chunkBoundingBoxes[i][j][k].max.x + blockOffset), blocks.length),
                                Math.min((int)(chunkBoundingBoxes[i][j][k].max.y + blockOffset), blocks[0].length),
                                Math.min((int)(chunkBoundingBoxes[i][j][k].max.z + blockOffset), blocks[0][0].length));
                        closestModelInstance = chunkResult.key;
                        minDistance = chunkResult.value;
                        
                    }
                }
            }
        }

        if (closestModelInstance == null) {
            return Clickable.NULL;
        }


        ModelInstance closestModelInstance_final = closestModelInstance;
        intersection.set(closestIntersection);

        Gdx.app.debug("GameState.getClickableOnRay",
                "Returning clickable for block at i: " + closestCoords.x +
                        " j: " + closestCoords.y +
                        " k: " + closestCoords.z +
                        " intersection: " + intersection);
        
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
            public void setHovered(boolean hovered) {
                Material mat = closestModelInstance_final.materials.get(0);
                mat.clear();
                if (hovered) {
                    mat.set(Unit.hoveredMaterial);
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
    
    
    public int[] getWorldDimensions() {
       return new int[]{blocks.length, blocks[0].length, blocks[0][0].length};
    }

    @Override
    public void render(RenderConfig config) {
        if (updateCache) {
            modelCache.begin();
            for (ModelInstance[][] instanceArr2: instances) {
                for (ModelInstance[] instanceArr: instanceArr2) {
                    for (ModelInstance instance: instanceArr) {
                        if (instance != null) {
                            modelCache.add(instance);
                        }
                    }
                }
            }
            modelCache.end();
            updateCache = false;
        }
        ModelBatch batch = config.getModelBatch();
        Environment env = config.getEnv();
        batch.begin(config.getCam());
        batch.render(modelCache, env);
        batch.end();

    }
}
