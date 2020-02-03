package com.week1.game.Model.World;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import static com.week1.game.GameScreen.PIXELS_PER_UNIT;

interface Block {

    TextureRegion getTextureRegion();
    Array<Connection<Block>> getConnections();
    void setConnection(Connection<Block> neighbor);
    int getIndex();
    float getCost();
    class TerrainBlock implements Block {
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
        static class AirBlock extends TerrainBlock{

            AirBlock() {
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
    }
}



