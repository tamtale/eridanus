package com.week1.game.Model.Components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

import static com.week1.game.Model.Initializer.bmfData;
import static com.week1.game.Model.Initializer.fontPixmap;

public class RenderNametagComponent {
    private Decal nametag = null;
    private String name;
    
    public RenderNametagComponent(String name) {
        this.name = name;
    }
    
    public Decal nametag() {
        if (nametag == null) {
            nametag = makeNametag(name);
        }
        
        return nametag;
    }


    private Decal makeNametag(String str) {
        // Compute the required width of the pixmap
        int requiredWidth = 0;
        for (int i = 0; i < str.length(); i++) {
            requiredWidth += bmfData.getGlyph(str.charAt(i)).width;
        }

        // Initialize the map
        int mapHeight = 20;
        Pixmap map = new Pixmap(requiredWidth, mapHeight, Pixmap.Format.RGBA8888);
//        map.setColor(100, 0,0,100); // tint the background
        map.setColor(0,0,0,255);
        map.fill();

        // Draw the characters on the pixmap
        int cursorLocation = 0;
        for (int i = 0; i < str.length(); i++) {
            BitmapFont.Glyph letter = bmfData.getGlyph(str.charAt(i));
            map.drawPixmap(fontPixmap, cursorLocation,  (-1 * letter.height) - letter.yoffset, letter.srcX, letter.srcY, letter.width, letter.height);
            cursorLocation += letter.width;
        }

        Texture textTexture = new Texture(map);
        TextureRegion textTextureRegion = new TextureRegion(textTexture);

        float scaleFactor = 0.1f;
        Decal nametag = Decal.newDecal(textTextureRegion.getRegionWidth() * scaleFactor, textTextureRegion.getRegionHeight() * scaleFactor, textTextureRegion, true);

        return nametag;
    }
}
