package com.week1.game.Model.Components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

import java.util.HashMap;
import java.util.Map;

import static com.week1.game.Model.Initializer.bmfData;
import static com.week1.game.Model.Initializer.fontPixmap;

public class RenderNametagComponent {
    private Decal nametag = null;
    private String name;
    private Color color;
    
    public RenderNametagComponent(String name, Color color) {
        this.name = name;
        this.color = color;
    }
    
    public Decal nametag() {
        if (nametag == null) {
            nametag = makeNametag(name, color);
        }
        
        return nametag;
    }


    private static Decal makeNametag(String str, Color c) {
        // Compute the required width of the pixmap
        int requiredWidth = 0;
        for (int i = 0; i < str.length(); i++) {
            requiredWidth += bmfData.getGlyph(str.charAt(i)).width;
        }

        // Colors are on a scale from 0 - 1.0
        // for alpha, 1 is completely dark, 0 is clear
        
        // Initialize the map
        int mapHeight = 20;
        Pixmap map = new Pixmap(requiredWidth, mapHeight, Pixmap.Format.RGBA8888);
        map.setColor(0f,0f,0f,0f); // clear background
        map.fill();

        // Draw the characters on the pixmap
        int cursorLocation = 0;
        for (int i = 0; i < str.length(); i++) {
            BitmapFont.Glyph letter = bmfData.getGlyph(str.charAt(i));
            map.drawPixmap(fontPixmap, cursorLocation,  (-1 * letter.height) - letter.yoffset, letter.srcX, letter.srcY, letter.width, letter.height);
            cursorLocation += letter.width;
        }

        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {

                // Determine the value of the original shade
                int origColor = map.getPixel(i,j);
                Color originalColor = new Color(origColor);
                float avgIntensity = (originalColor.r + originalColor.g + originalColor.b) / 3f;

                // Make a tinted color that will match the value of the original shade
                Color newC = new Color(c.r * avgIntensity, c.g * avgIntensity, c.b * avgIntensity, originalColor.a);
                map.setColor(newC);

                map.drawPixel(i,j);
            }
        }

        Texture textTexture = new Texture(map);
        TextureRegion textTextureRegion = new TextureRegion(textTexture);

        float scaleFactor = 0.1f;
        Decal nametag = Decal.newDecal(textTextureRegion.getRegionWidth() * scaleFactor, textTextureRegion.getRegionHeight() * scaleFactor, textTextureRegion, true);

        return nametag;
    }
}
