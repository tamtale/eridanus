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

    class TerrainBlock implements Block {
        private TextureRegion textureRegion;
        private int index;
        private Array<Connection<TerrainBlock>> edges;
        TerrainBlock(Color color) {
            Pixmap unitPixmap = new Pixmap(PIXELS_PER_UNIT, PIXELS_PER_UNIT, Pixmap.Format.RGB888);
            unitPixmap.setColor(color);
            unitPixmap.fill();
            this.textureRegion = new TextureRegion(new Texture(unitPixmap));
        }
        public static TerrainBlock AIR = new TerrainBlock(Color.GOLD);
        public static TerrainBlock STONE = new TerrainBlock(Color.GRAY);

        @Override
        public TextureRegion getTextureRegion() {
            return textureRegion;
        }

        public int getIndex() {
            return index;
        }

        public Array<Connection<TerrainBlock>> getConnections(){
            return edges;
        }
    }


    class TowerBlock implements Block {

        @Override
        public TextureRegion getTextureRegion() {
            return null;
        }
    }
}



