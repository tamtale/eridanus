package com.week1.game.Model.World;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import static com.week1.game.GameController.PIXELS_PER_UNIT;

interface Block {

    TextureRegion getTextureRegion();

    class TerrainBlock implements Block {
        private TextureRegion textureRegion;
        TerrainBlock(Color color) {
            Pixmap unitPixmap = new Pixmap(PIXELS_PER_UNIT, PIXELS_PER_UNIT, Pixmap.Format.RGB888);
            unitPixmap.setColor(color);
            unitPixmap.fill();
            unitPixmap.setColor(Color.GRAY);
            unitPixmap.fillRectangle(3, 3, PIXELS_PER_UNIT - 6, PIXELS_PER_UNIT - 6);
            this.textureRegion = new TextureRegion(new Texture(unitPixmap));
        }
        public static TerrainBlock AIR = new TerrainBlock(Color.GOLD);
        public static TerrainBlock STONE = new TerrainBlock(Color.BLACK);

        @Override
        public TextureRegion getTextureRegion() {
            return textureRegion;
        }
    }

    class TowerBlock implements Block {

        @Override
        public TextureRegion getTextureRegion() {
            return null;
        }
    }
}



