package com.week1.game.Model.World;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.Initializer;
import com.week1.game.TowerBuilder.BlockType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
public interface Block {
    float getCost();
    Optional<ModelInstance> modelInstance(float x, float y, float z);
    boolean canSupportTower();
    boolean allowMinionToSpawnOn();

    abstract class TerrainBlock implements Block {
        Model model;
        private float cost;

        TerrainBlock(Color color, float cost) {
            this.cost = cost;
            this.model = BUILDER.createBox(1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(color)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        }

        @Override
        public boolean canSupportTower() {
            return true;
        }
        
        @Override
        public boolean allowMinionToSpawnOn() {
            return true;
        }

        @Override
        public float getCost() {
            return cost;
        }

        private static ModelBuilder BUILDER = new ModelBuilder();
        public static TerrainBlock AIR = new TerrainBlock(Color.GOLD, 0) {
            @Override
            public Optional<ModelInstance> modelInstance(float x, float y, float z) {
                return Optional.empty();
            }
            @Override
            public boolean canSupportTower() {
                // Air shouldn't be able to hold up a tower
                return false;
            }

            @Override
            public boolean allowMinionToSpawnOn() {
                return false;
            }
        };
        public static TerrainBlock STONE = new TerrainBlock(Color.GRAY, 1) {

            @Override
            public Optional<ModelInstance> modelInstance(float x, float y, float z) {
                ModelInstance instance = new ModelInstance(model);
                instance.transform.translate(x, y, z);
                return Optional.of(instance);
            }
        };
        public static TerrainBlock DIRT = new TerrainBlock(Color.BROWN, 1.5f) {
            @Override
            public Optional<ModelInstance> modelInstance(float x, float y, float z) {
                ModelInstance instance = new ModelInstance(model);
                instance.transform.setToTranslation(x, y, z);
                return Optional.of(instance);
            }
        };

        public static TerrainBlock FIREBRICK = new TerrainBlock(Color.FIREBRICK, 1.5f) {
            @Override
            public Optional<ModelInstance> modelInstance(float x, float y, float z) {
                ModelInstance instance = new ModelInstance(model);
                instance.transform.setToTranslation(x, y, z);
                return Optional.of(instance);
            }
        };

        public static TerrainBlock ZAMBALA = new TerrainBlock(Color.CORAL, 1f) {
            @Override
            public Optional<ModelInstance> modelInstance(float x, float y, float z) {
                ModelInstance instance = new ModelInstance(model);
                instance.transform.setToTranslation(x, y, z);
                return Optional.of(instance);
            }
        };

        public static TerrainBlock WATER = new TerrainBlock(Color.CYAN, 5f) {
            @Override
            public Optional<ModelInstance> modelInstance(float x, float y, float z) {
                ModelInstance instance = new ModelInstance(model);
                instance.transform.setToTranslation(x, y, z);
                return Optional.of(instance);
            }

            @Override
            public boolean canSupportTower() {
                return false;
            }
        };

    }


    class TowerBlock implements Block {

        Model model;

        TowerBlock(Model model) {
            this.model = model;
        }

        @Override
        public boolean canSupportTower() {
            return false;
        }
        
        @Override
        public boolean allowMinionToSpawnOn() {
            return false;
        }

        @Override
        public float getCost() {
            return 1.5f;
        }

        @Override
        public Optional<ModelInstance> modelInstance(float x, float y, float z) {
            ModelInstance instance = new ModelInstance(model);
            instance.transform.setToTranslation(x, y, z);
            return Optional.of(instance);
        }

        public static Map<BlockType, TowerBlock> towerBlockMap = new HashMap<BlockType, TowerBlock>() {{
            put(BlockType.WATER, new TowerBlock(Initializer.waterBlock));
            put(BlockType.MOONSTONE, new TowerBlock(Initializer.moonStone));
            put(BlockType.OBSIDIAN, new TowerBlock(Initializer.spaceObsidian));
            put(BlockType.SPACEGOLD, new TowerBlock(Initializer.spaceGold));
            put(BlockType.EARTH, new TowerBlock(Initializer.earthBlock));
            put(BlockType.FIRE, new TowerBlock(Initializer.fireBlock));
            put(BlockType.SPAWNER, new TowerBlock(Initializer.spawner));
            put(BlockType.EASTEREGG, new TowerBlock(Initializer.easterEgg));
        }};
    }
}



