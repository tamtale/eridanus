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
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.Initializer;
import com.week1.game.TowerBuilder.BlockType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface Block {

    TextureRegion getTextureRegion();

    Array<Connection<Block>> getConnections();
    void setConnection(Connection<Block> neighbor);
    int getIndex();
    float getCost();
    Vector3 getCoords();
    void setCoords(Vector3 coords);
    void setIndex(int nodeCount);
    Optional<ModelInstance> modelInstance(float x, float y, float z);

    abstract class TerrainBlock implements Block {
        private Vector3 coords;
        private TextureRegion textureRegion;
        private int index;
        private Color color;
        Model model;

        private static ModelBuilder BUILDER = new ModelBuilder();
        public static TerrainBlock AIR = new TerrainBlock(Color.GOLD) {
            @Override
            public float getCost(){
                return 0;
            }

            @Override
            public Optional<ModelInstance> modelInstance(float x, float y, float z) {
                return Optional.empty();
            }

        };
        public static TerrainBlock STONE = new TerrainBlock(Color.GRAY) {
            @Override
            public float getCost(){
                return 1;
            }

            @Override
            public Optional<ModelInstance> modelInstance(float x, float y, float z) {
                ModelInstance instance = new ModelInstance(model);
                instance.transform.translate(x, y, z);
                return Optional.of(instance);
            }
        };
        public static TerrainBlock DIRT = new TerrainBlock(Color.BROWN) {
            @Override
            public float getCost(){
                return 1.5f;
            }

            @Override
            public Optional<ModelInstance> modelInstance(float x, float y, float z) {
                ModelInstance instance = new ModelInstance(model);
                instance.transform.setToTranslation(x, y, z);
                return Optional.of(instance);
            }
        };


//        public static TerrainBlock REDBLOCK = new TerrainBlock("water2.png") {
//            @Override
//            public float getCost(){
//                return 1.5f;
//            }
//
//            @Override
//            public Optional<ModelInstance> modelInstance(float x, float y, float z) {
//                ModelInstance instance = new ModelInstance(model);
//                instance.transform.setToTranslation(x, y, z);
//                return Optional.of(instance);
//            }
//        };

        private Array<Connection<Block>> edges;
        
        TerrainBlock(Color color) {
            this.edges = new Array<>();
            this.model = BUILDER.createBox(1f, 1f, 1f,
                    new Material(ColorAttribute.createDiffuse(color)),
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        }


//        TerrainBlock(String textureFilename) {
//            this.edges = new Array<>();
////            this.model = BUILDER.createBox(1f, 1f, 1f,
////                    new Material(ColorAttribute.createDiffuse(Color.GREEN)),
////                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
//
//            this.model = BUILDER.createBox(1f, 1f, 1f,
//                    new Material(TextureAttribute.createDiffuse(new Texture(textureFilename))),
//                    VertexAttributes.Usage.Position |VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal);
//
//        }
        

        @Override
        public TextureRegion getTextureRegion() {
            return textureRegion;
        }

        public float getCost(){
            return 1;
        }

        @Override
        public Vector3 getCoords() {
            return coords;
        }

        @Override
        public void setCoords(Vector3 coords) {
            this.coords = coords;
        }

        @Override
        public void setIndex(int nodeCount) {
            this.index = nodeCount;
        }

        public int getIndex() {
            return index;
        }

        public Array<Connection<Block>> getConnections(){
            return edges;
        }

        @Override
        public void setConnection(Connection<Block> neighbor) {
            edges.add(neighbor);
        }
    }


    class TowerBlock implements Block {
        private Array<Connection<Block>> edges;
        private int index;
        private Vector3 coords;
        public TowerBlock(Vector3 coords) {
            this.coords = coords;
        }
        Model model;


        @Override
        public TextureRegion getTextureRegion() {
            return null;
        }

        @Override
        public Array<Connection<Block>> getConnections() {
            return edges;
        }

        @Override
        public void setConnection(Connection<Block> neighbor) {
            edges.add(neighbor);
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public float getCost() {
            return Float.POSITIVE_INFINITY;
        }

        @Override
        public Vector3 getCoords() {
            return coords;
        }

        @Override
        public void setCoords(Vector3 coords) {
            this.coords = coords;
        }

        @Override
        public void setIndex(int nodeCount) {
            this.index = nodeCount;
        }

        @Override
        public Optional<ModelInstance> modelInstance(float x, float y, float z) {
            // TODO this
            return Optional.empty();
        }

        public static Map<BlockType, TowerBlock> towerBlockMap = new HashMap<BlockType, TowerBlock>() {{
            this.put(BlockType.WATER, new TowerBlock() {
                @Override
                public float getCost(){
                    return 1.5f;
                }

                @Override
                public Optional<ModelInstance> modelInstance(float x, float y, float z) {
                    ModelInstance instance = new ModelInstance(Initializer.waterBlock);
                    instance.transform.setToTranslation(x, y, z);
                    return Optional.of(instance);
                }
            });

            this.put(BlockType.MOONSTONE, new TowerBlock() {
                @Override
                public float getCost(){
                    return 1.5f;
                }

                @Override
                public Optional<ModelInstance> modelInstance(float x, float y, float z) {
                    ModelInstance instance = new ModelInstance(Initializer.moonStone);
                    instance.transform.setToTranslation(x, y, z);
                    return Optional.of(instance);
                }
            });
            
            this.put(BlockType.OBSIDIAN, new TowerBlock() {
                @Override
                public float getCost(){
                    return 1.5f;
                }

                @Override
                public Optional<ModelInstance> modelInstance(float x, float y, float z) {
                    ModelInstance instance = new ModelInstance(Initializer.spaceObsidian);
                    instance.transform.setToTranslation(x, y, z);
                    return Optional.of(instance);
                }
            });
            
            this.put(BlockType.SPACEGOLD, new TowerBlock() {
                @Override
                public float getCost(){
                    return 1.5f;
                }

                @Override
                public Optional<ModelInstance> modelInstance(float x, float y, float z) {
                    ModelInstance instance = new ModelInstance(Initializer.spaceGold);
                    instance.transform.setToTranslation(x, y, z);
                    return Optional.of(instance);
                }
            });
            
            this.put(BlockType.EARTH, new TowerBlock() {
                @Override
                public float getCost(){
                    return 1.5f;
                }

                @Override
                public Optional<ModelInstance> modelInstance(float x, float y, float z) {
                    ModelInstance instance = new ModelInstance(Initializer.earthBlock);
                    instance.transform.setToTranslation(x, y, z);
                    return Optional.of(instance);
                }
            });

            this.put(BlockType.FIRE, new TowerBlock() {
                @Override
                public float getCost(){
                    return 1.5f;
                }

                @Override
                public Optional<ModelInstance> modelInstance(float x, float y, float z) {
                    ModelInstance instance = new ModelInstance(Initializer.fireBlock);
                    instance.transform.setToTranslation(x, y, z);
                    return Optional.of(instance);
                }
            });

            this.put(BlockType.EASTEREGG, new TowerBlock() {
                @Override
                public float getCost(){
                    return 1.5f;
                }

                @Override
                public Optional<ModelInstance> modelInstance(float x, float y, float z) {
                    ModelInstance instance = new ModelInstance(Initializer.easterEgg);
                    instance.transform.setToTranslation(x, y, z);
                    return Optional.of(instance);
                }
            });
            
        }};


        TowerBlock() {
            this.edges = new Array<>();
//            this.model = BUILDER.createBox(1f, 1f, 1f,
//                    new Material(ColorAttribute.createDiffuse(Color.GREEN)),
//                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

//            this.model = BUILDER.createBox(1f, 1f, 1f,
//                    new Material(TextureAttribute.createDiffuse(new Texture(textureFilename))),
//                    VertexAttributes.Usage.Position |VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Normal);

        }

    }
}



