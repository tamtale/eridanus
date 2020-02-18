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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

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

        private Array<Connection<Block>> edges;
        TerrainBlock(Color color) {
            this.edges = new Array<>();
            this.model = BUILDER.createBox(1f, 1f, 1f,
                    new Material(ColorAttribute.createDiffuse(color)),
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        }



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
    }
}



