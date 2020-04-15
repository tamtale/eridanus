package com.week1.game.Model.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.Entities.Clickable;
import com.week1.game.Model.Entities.Unit;
import com.week1.game.Pair;
import com.week1.game.Renderer.GameRenderable;
import com.week1.game.Renderer.RenderConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.week1.game.Model.Initializer.blueMaterial;
import static com.week1.game.Model.Initializer.clearMaterial;

public class GameWorld implements GameRenderable {
    private Block[][][] blocks;
    private int LENGTH, WIDTH, HEIGHT;
    private static int CHUNKSIZE = 256;
    private boolean[] shouldRefreshChunk;
    private ModelCache[] chunkedModelCaches;
    private ModelInstance[] modelInstances;
    private int[][] heightMap;
    private boolean refreshHeight = true; // whether or not the map has changed, warranting a new height map.
    private GameGraph graph;
    private BoundingBox[][][] boundingBoxes;

    private BoundingBox[][][] chunkBoundingBoxes;
    private int[][][] activeBlocksPerChunk;
    
    private Material[][][] originalMaterials;
    private boolean[][] visible;

    public static final float blockOffset = 0.5f;

    private int chunkSide;
    private int chunkHeight;
    
    private Array<Pair<Integer, Integer>> recentlyChangedLocations = new Array<>();

    public GameWorld(IWorldBuilder worldBuilder) {
        blocks = worldBuilder.terrain();
        this.graph = new GameGraph(blocks);
        LENGTH = blocks.length;
        WIDTH = blocks[0].length;
        HEIGHT = blocks[0][0].length;
        for (int i = 0; i < LENGTH; i++) {
            for (int j = 0; j < WIDTH; j++) {
                graph.addVector2(new Vector2(i, j));
            }
        }
        Gdx.app.debug("Game World - wab2", "Block array built");
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

        // Build the modelinstances and precompute the bounding boxes.
        modelInstances = new ModelInstance[blocks.length * blocks[0].length * blocks[0][0].length];
        boundingBoxes = new BoundingBox[blocks.length][blocks[0].length][blocks[0][0].length];
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                for (int k = 0; k < blocks[0][0].length; k++) {
                    int i_final = i;
                    int j_final = j;
                    int k_final = k;
                    blocks[i][j][k].modelInstance(i, j, k)
                            .ifPresent(modelInstance -> {
                                setModelInstance(i_final, j_final, k_final, modelInstance);
                            });

                    boundingBoxes[i][j][k] = new BoundingBox();
                    updateBoundingBox(i,j,k);
                    updateActiveBlocks(i, j, k);
                }
            }
        }

        // Create the refresh/modelcache chunks.
        shouldRefreshChunk = new boolean[LENGTH * WIDTH * HEIGHT / CHUNKSIZE + 1];
        chunkedModelCaches = new ModelCache[shouldRefreshChunk.length];
        for (int i = 0; i < shouldRefreshChunk.length; i++) {
            chunkedModelCaches[i] = new ModelCache();
        }
        Arrays.fill(shouldRefreshChunk, true);
        
        // Fill up the original materials array
        originalMaterials = new Material[LENGTH][WIDTH][HEIGHT];
        visible = new boolean[LENGTH][WIDTH];
        for (int i = 0; i < LENGTH; i++) {
            for (int j = 0; j < WIDTH; j++) {
                visible[i][j] = false;
                for (int k = 0; k < HEIGHT; k++) {
                    ModelInstance instance = getModelInstance(i, j, k);
                    if (instance != null) {
                        originalMaterials[i][j][k] = instance.model.materials.get(0);
                    }
                }
            }
        }
    }

    private void updateActiveBlocks(int i, int j, int k) {

        int chunkX = i / chunkSide;
        int chunkY = j / chunkSide;
        int chunkZ = k / chunkHeight;


        if (getModelInstance(i, j, k) == null) {
            activeBlocksPerChunk[chunkX][chunkY][chunkZ]--;
        } else {
            activeBlocksPerChunk[chunkX][chunkY][chunkZ]++;
        }
    }

    private ModelInstance getModelInstance(int i, int j, int k) {
        return modelInstances[i * WIDTH * HEIGHT + j * HEIGHT + k];
    }

    private void setModelInstance(int i, int j, int k, ModelInstance instance) {
        modelInstances[i * WIDTH * HEIGHT + j * HEIGHT + k] = instance;
    }


    private void updateBoundingBox(int i, int j, int k) {
        ModelInstance instance = getModelInstance(i, j, k);
        if (instance != null) {
            instance.calculateBoundingBox(boundingBoxes[i][j][k]);
            boundingBoxes[i][j][k].mul(instance.transform);
        }
    }

    private Lock hidesLock = new ReentrantLock();
    private List<Pair<Integer,Integer>> hides = new ArrayList<>();
    public void markForHide(int i, int j) {
        hidesLock.lock();
        hides.add(new Pair<>(i, j));
        hidesLock.unlock();
    }
    private Lock unhidesLock = new ReentrantLock();
    private List<Pair<Integer,Integer>> unhides = new ArrayList<>();
    public void markForUnhide(int i, int j) {
        unhidesLock.lock();
        unhides.add(new Pair<>(i, j));
        unhidesLock.unlock();
    }
    
    
    public void hideColumn(int i, int j) {
        visible[i][j] = false;
        for (int k = 0; k < HEIGHT; k++) {
            hideBlock(i,j,k);
        }
    }
    private void hideBlock(int i, int j, int k) {
        ModelInstance modelInstance = getModelInstance(i, j, k);

//             Only need to hide the block if not air (otherwise, just let it stay air)
        if (modelInstance != null) {

            if (blocks[i][j][k] instanceof Block.TowerBlock) { // hide towers completely
                setModelInstance(i, j, k, null);
//                    Material mat = modelInstance.materials.get(0);
//                    mat.clear();
//
//                    mat.set(clearMaterial);
            } else { // just turn Terrain Blocks black
                Material mat = modelInstance.materials.get(0);
                mat.clear();

                mat.set(blueMaterial);
            }
            shouldRefreshChunk[((i * WIDTH * HEIGHT + j * HEIGHT + k)) / CHUNKSIZE] = true;
        }
    }

    public void unhideColumn(int i, int j) {
        visible[i][j] = true;
        for (int k = 0; k < HEIGHT; k++) {
            unhideBlock(i, j, k);
        }
    }
    
    public void unhideBlock(int i, int j, int k) {
        // Don't need to unhide air
        if (blocks[i][j][k] instanceof Block.TowerBlock) {
            setModelInstance(i,j,k,blocks[i][j][k].modelInstance(i,j,k).orElse(null));
        } else {
            ModelInstance modelInstance = getModelInstance(i, j, k);
            if (modelInstance != null) {
                Material mat = modelInstance.materials.get(0);
                mat.clear();

                mat.set(originalMaterials[i][j][k]);
//                mat.set(showMaterial);
            }
        }
        shouldRefreshChunk[((i * WIDTH * HEIGHT + j * HEIGHT + k)) / CHUNKSIZE] = true;
    }

    /*
     * Clears the locations after checking
     */
    public Array<Pair<Integer, Integer>> pollRecentlyChangedLocations() {
        Array<Pair<Integer, Integer>> ret = this.recentlyChangedLocations;
        this.recentlyChangedLocations = new Array<>();
        return ret;
    }
    
    public Block getBlock(int i, int j, int k) {
        return blocks[i][j][k];
    }
    
    public void addTowerBlock(int i, int j, int k, Block.TowerBlock block, boolean locallyOwned) {
        // Keep track of recently updated blocks, so that the fog system can appropriately hide/show
        Pair<Integer, Integer> newLoc = new Pair<>(i, j);
        if (!recentlyChangedLocations.contains(newLoc, false)) {
            recentlyChangedLocations.add(newLoc);
        }

        blocks[i][j][k] = block;
        Optional<ModelInstance> modelInstance = blocks[i][j][k].modelInstance(i,j,k);
        if (modelInstance.isPresent()) {
            if (locallyOwned) { // if the tower is locally owned, show the blocks immediately
                setModelInstance(i, j, k, modelInstance.get());
                originalMaterials[i][j][k] = modelInstance.get().model.materials.get(0);
            } else { // if the tower is owned by an opponent, only show the blocks once confirmed by fog system
                setModelInstance(i, j, k, null);
                originalMaterials[i][j][k] = modelInstance.get().model.materials.get(0);
            }
        } else {
            setModelInstance(i, j, k, null);
            originalMaterials[i][j][k] = null;
        }
        updateBoundingBox(i,j,k);
        updateActiveBlocks(i,j,k);
        updateGraph(i, j, block);
        refreshHeight = true;
        shouldRefreshChunk[(i * WIDTH * HEIGHT +  j * HEIGHT + k) / CHUNKSIZE] = true;

    }
    
    public void clearBlock(int i, int j, int k) {
        Block block = Block.TerrainBlock.AIR;
        blocks[i][j][k] = block;
        Optional<ModelInstance> modelInstance = blocks[i][j][k].modelInstance(i,j,k);
//        if (modelInstance.isPresent()) {
//            setModelInstance(i, j, k, modelInstance.get());
//            originalMaterials[i][j][k] = modelInstance.get().model.materials.get(0);
//        } else {
            setModelInstance(i, j, k, null);
            originalMaterials[i][j][k] = null;
//        }
        updateBoundingBox(i,j,k);
        updateActiveBlocks(i,j,k);
        updateGraph(i, j, block);
        refreshHeight = true;
        shouldRefreshChunk[(i * WIDTH * HEIGHT +  j * HEIGHT + k) / CHUNKSIZE] = true;
    }

    private void updateGraph(int i, int j, Block block) {
        getHeightMap();
        int k = heightMap[i][j];
        for (int m = i - 1; m <= i + 1; m++) {
            for (int n = j - 1; n <= j + 1; n++) {
                if ((m != i || n != j) && (m >= 0 && m < heightMap.length && n >= 0 && n < heightMap[0].length)) {
                    graph.removeConnection(m, n, i, j);
                    //graph.removeConnection(i, j, m, n);
                    if (heightMap[m][n] + 1 >= k){
                        graph.setConnection(new Vector2(m, n), new Vector2(i, j), block.getCost());
                        graph.setConnection(new Vector2(i, j), new Vector2(m, n), blocks[m][n][heightMap[m][n]].getCost());
                    }
                }
            }
        }

    }

    public GameGraph buildGraph(){

        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
//                for (int k = 0; k < blocks[0][0].length; k++) {
                    Vector2 coords = new Vector2(i, j);
                    if (i > 0 && (heightMap[i][j] + 1 >= heightMap[i - 1][j])) {
                        float cost = blocks[i][j][heightMap[i][j]].getCost();
                        if (heightMap[i][j] < heightMap[i - 1][j]){
                            cost *= 2;
                        } if (heightMap[i][j] > heightMap[i - 1][j]){
                            cost *= 1.5;
                        }
                        graph.setConnection(coords, new Vector2(i - 1, j), cost);
                    }
                    if (i < blocks.length - 1 && (heightMap[i][j] + 1 >= heightMap[i + 1][j])) {
                        float cost = blocks[i][j][heightMap[i][j]].getCost();
                        if (heightMap[i][j] < heightMap[i + 1][j]){
                            cost *= 2;
                        } if (heightMap[i][j] > heightMap[i + 1][j]){
                            cost *= 1.5;
                        }
                        graph.setConnection(coords, new Vector2(i + 1, j), cost);
                    }

                    if (j > 0 && (heightMap[i][j] + 1 >= heightMap[i][j - 1])) {
                        float cost = blocks[i][j][heightMap[i][j]].getCost();
                        if (heightMap[i][j] < heightMap[i][j - 1]){
                            cost *= 2;
                        } if (heightMap[i][j] > heightMap[i][j - 1]){
                            cost *= 1.5;
                        }
                        graph.setConnection(coords, new Vector2(i, j - 1), cost);
                    }
                    if(j < blocks[0].length - 1 && (heightMap[i][j] + 1 >= heightMap[i][j + 1])) {
                        float cost = blocks[i][j][heightMap[i][j]].getCost();
                        if (heightMap[i][j] < heightMap[i][j + 1]){
                            cost *= 2;
                        } if (heightMap[i][j] > heightMap[i][j + 1]){
                            cost *= 1.5;
                        }
                        graph.setConnection(coords, new Vector2(i, j + 1), cost);
                    }
                    if (i > 0 && j > 0
                            && (heightMap[i][j] + 1 >= heightMap[i - 1][j - 1])) {
                        float cost = blocks[i][j][heightMap[i][j]].getCost();
                        if (heightMap[i][j] < heightMap[i - 1][j - 1]){
                            cost *= 2;
                        } if (heightMap[i][j] > heightMap[i - 1][j - 1]){
                            cost *= 1.5;
                        }
                        graph.setConnection(coords, new Vector2(i - 1, j - 1),
                                cost * (float) Math.sqrt(2));
                    }
                    if (i > 0 && j < blocks[0].length - 1
                            && (heightMap[i][j] + 1 >= heightMap[i - 1][j + 1])) {
                        float cost = blocks[i][j][heightMap[i][j]].getCost();
                        if (heightMap[i][j] < heightMap[i - 1][j + 1]){
                            cost *= 2;
                        } if (heightMap[i][j] > heightMap[i - 1][j + 1]){
                            cost *= 1.5;
                        }
                        graph.setConnection(coords, new Vector2(i - 1, j + 1),
                                cost * (float) Math.sqrt(2));
                    }
                    if (i < blocks.length - 1 && j > 0
                            && (heightMap[i][j] + 1 >= heightMap[i + 1][j - 1])) {
                        float cost = blocks[i][j][heightMap[i][j]].getCost();
                        if (heightMap[i][j] < heightMap[i + 1][j - 1]){
                            cost *= 2;
                        } if (heightMap[i][j] > heightMap[i + 1][j - 1]){
                            cost *= 1.5;
                        }
                        graph.setConnection(coords, new Vector2(i + 1, j - 1),
                                cost * (float) Math.sqrt(2));
                    }
                    if (i < blocks.length  - 1 && j < blocks[0].length - 1
                            && (heightMap[i][j] + 1 >= heightMap[i + 1][j + 1])) {
                        float cost = blocks[i][j][heightMap[i][j]].getCost();
                        if (heightMap[i][j] < heightMap[i + 1][j + 1]){
                            cost *= 2;
                        } if (heightMap[i][j] > heightMap[i + 1][j + 1]){
                            cost *= 1.5;
                        }
                        graph.setConnection(coords, new Vector2(i + 1, j + 1),
                                cost * (float) Math.sqrt(2));
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
                    ModelInstance modelInstance = getModelInstance(i, j, k);
                    // Ignore the null model instances, which correspond to empty spaces in the map
                    if (modelInstance == null) {
                        continue;
                    }
                    nonNull++;

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

        return new Pair<>(closestModelInstance, minDistance);
    }

    /*
        Returns the closest block to the camera that intersects with the given ray.
     */
    public Clickable getBlockOnRay(Ray ray, Vector3 intersection) {

        // If too slow again, could try maintaining a 'visible' group of blocks, which excludes blocks that are buried under others and can't be clicked

        // Search in chunks
        Vector3 closestIntersection = new Vector3();
        BoundingBox closestBox = new BoundingBox();
        ModelInstance closestModelInstance = null;
        float minDistance = Float.MAX_VALUE;
        Vector3 closestCoords = new Vector3();

        Vector3 throwAway = new Vector3();
        for (int i = 0; i < chunkBoundingBoxes.length; i++) {
            for (int j = 0; j < chunkBoundingBoxes[0].length; j++) {
                for (int k = 0; k < chunkBoundingBoxes[0][0].length; k++) {
                    if (activeBlocksPerChunk[i][j][k] == 0) {
//                        System.out.println("jIgnoring chunk, because it contains no active blocks.");
                        continue;
                    }

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

//        System.out.println("Is the originalMaterial null? " + originalMaterials[(int)closestCoords.x][(int)closestCoords.y][(int)closestCoords.z]);
//        System.out.println("But what about the modelinstance: " + closestModelInstance_final.model.materials.get(0));
        return new Clickable() {
            private BoundingBox boundingBox = new BoundingBox(closestBox);
            private int x = (int)closestCoords.x;
            private int y = (int)closestCoords.y;
            private int z = (int)closestCoords.z;

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
                    mat.set(originalMaterials[x][y][z]);
                }
                shouldRefreshChunk[((x * WIDTH * HEIGHT + y * HEIGHT + z)) / CHUNKSIZE] = true;
            }

            @Override
            public void setHovered(boolean hovered) {
                if (hovered) {
                    Material mat = closestModelInstance_final.materials.get(0);
                    mat.clear();
                    mat.set(Unit.hoveredMaterial);
                    shouldRefreshChunk[( (x * WIDTH * HEIGHT + y * HEIGHT + z)) / CHUNKSIZE] = true;
                } else { // these take care of refreshing the chunk in unhide/hide
                    if (visible[x][y]) {
                        unhideBlock(x,y,z);
                    } else {
                        hideBlock(x,y,z);
                    }
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
    
    
    
    
    
    /*
     * Fetches the current state of the given chunk and updates the modelcache for that chunk.
     */
    public void refreshChunkModelCache(int idx) {
        chunkedModelCaches[idx].begin();
        for (int i = CHUNKSIZE * idx; i < CHUNKSIZE * (idx + 1) && i < LENGTH*WIDTH*HEIGHT; i++) {
            if (modelInstances[i] != null) {
                chunkedModelCaches[idx].add(modelInstances[i]);
            }
        }
        chunkedModelCaches[idx].end();
    }

    @Override
    public void render(RenderConfig config) {
        // change stuff
        hidesLock.lock();
        hides.forEach((hidePair) -> hideColumn(hidePair.key, hidePair.value));
        hides = new ArrayList<>();
        hidesLock.unlock();
        
        unhidesLock.lock();
        unhides.forEach((unhidePair) -> unhideColumn(unhidePair.key, unhidePair.value));
        unhides = new ArrayList<>();
        unhidesLock.unlock();
        
        
        
        // update
        for (int i = 0; i < chunkedModelCaches.length; i++) {
            if (shouldRefreshChunk[i]) {
                refreshChunkModelCache(i);
                shouldRefreshChunk[i] = false;
            }
        }

        
        // render
        ModelBatch batch = config.getModelBatch();
        Environment env = config.getEnv();
        batch.begin(config.getCam());
        for (int i = 0; i < chunkedModelCaches.length; i++) {
            batch.render(chunkedModelCaches[i], env);
        }

        batch.end();
//        try {
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//            System.out.println("uh oh");
//        }
        

//        System.out.println("null materials: " + anyNullMaterials());
    }
    
//    public void printAll(ModelCache[] modelCaches) {
//        System.out.println("Printing info: ");
//        for (int i = 0; i < modelCaches.length; i++) {
//            Array<Renderable> renderables = new Array<>();
//            modelCaches[i].getRenderables(renderables, null);
//            for (int j = 0; j < renderables.size; j++) {
//                Renderable r = renderables.get(j);
//                System.out.println("renderable: " + r + " fields: " + r.shader + ", " + 
//                        r.environment + ", " + 
//                        r.material + ", " + 
//                        r.meshPart + ", " + 
//                        r.userData);
//            }
//        }
//    }
//    
//    
//    public int anyNullMaterials() {
//        int numNullMaterials = 0;
//        for (int i = 0; i < chunkedModelCaches.length; i++) {
//            Array<Renderable> renderables = new Array<>();
//            chunkedModelCaches[i].getRenderables(renderables, null);
//            for (int j = 0; j < renderables.size; j++) {
//                if (renderables.get(j).material == null) {
//                    numNullMaterials++;
//                }
////                System.out.println("Shader: " + renderables.get(j).shader);
//            }
//        }
//        return numNullMaterials;
//    }

    public int getHeight(int i, int j) {
        getHeightMap();
        return heightMap[i][j];
    }
}
