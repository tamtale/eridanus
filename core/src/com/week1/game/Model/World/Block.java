package com.week1.game.Model.World;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import static com.week1.game.GameController.PIXELS_PER_UNIT;

interface Block {

    TextureRegion getTextureRegion();
    Array<Connection<Block>> getConnections();
    void setConnection(Connection<Block> neighbor);
    int getIndex();
    class TerrainBlock implements Block {
        private TextureRegion textureRegion;
        private int index;
        private Array<Connection<Block>> edges;
        TerrainBlock(Color color) {
            Pixmap unitPixmap = new Pixmap(PIXELS_PER_UNIT, PIXELS_PER_UNIT, Pixmap.Format.RGB888);
            unitPixmap.setColor(color);
            unitPixmap.fill();
            unitPixmap.fillRectangle(3, 3, PIXELS_PER_UNIT - 6, PIXELS_PER_UNIT - 6);
            unitPixmap.setColor(Color.GRAY);
            this.textureRegion = new TextureRegion(new Texture(unitPixmap));
        }
        public static TerrainBlock AIR = new TerrainBlock(Color.GOLD);
        public static TerrainBlock STONE = new TerrainBlock(Color.GRAY);
            ;




        @Override
        public TextureRegion getTextureRegion() {
            return textureRegion;
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
    }
}



