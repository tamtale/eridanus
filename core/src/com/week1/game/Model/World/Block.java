package com.week1.game.Model.World;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import static com.week1.game.GameScreen.PIXELS_PER_UNIT;

public interface Block {

    TextureRegion getTextureRegion();

    Array<Connection<Block>> getConnections();
    void setConnection(Connection<Block> neighbor);
    int getIndex();
    float getCost();
    Vector3 getCoords();

    void setIndex(int nodeCount);

    class TerrainBlock implements Block {
        private Vector3 coords;
        private TextureRegion textureRegion;
        private int index;
        private Array<Connection<Block>> edges;
        TerrainBlock(Color color) {
            Pixmap unitPixmap = new Pixmap(PIXELS_PER_UNIT, PIXELS_PER_UNIT, Pixmap.Format.RGB888);
            unitPixmap.setColor(color);
            unitPixmap.fill();
            unitPixmap.setColor(Color.GRAY);
            unitPixmap.fillRectangle(3, 3, PIXELS_PER_UNIT - 6, PIXELS_PER_UNIT - 6);
            this.textureRegion = new TextureRegion(new Texture(unitPixmap));
        }

        public TerrainBlock(Vector3 coords) {
            this.coords = coords;
        }

        static class AirBlock extends TerrainBlock{

            AirBlock(Vector3 vector3) {
                super(Color.GOLD);
            }

            @Override
            public float getCost(){
                return 0;
            }
        }
        static class StoneBlock extends TerrainBlock{

            StoneBlock(){
                super(Color.BLACK);
            }

            public StoneBlock(Vector3 vector3) {
                super(vector3);
            }

            @Override
            public float getCost(){
                return 1;
            }
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
        public void setIndex(int nodeCount) {
            this.index = nodeCount;
        }
    }
}



