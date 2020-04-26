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

        // Initialize the map
        int mapHeight = 20;
        Pixmap map = new Pixmap(requiredWidth, mapHeight, Pixmap.Format.RGBA8888);
        map.setColor(c.r, c.g, c.b, 100);
//        map.setColor(100, 0,0,100); // tint the background
//        map.setColor(0,0,0,255);
        map.fill();

        // Draw the characters on the pixmap
        int cursorLocation = 0;
        for (int i = 0; i < str.length(); i++) {
            BitmapFont.Glyph letter = bmfData.getGlyph(str.charAt(i));
            map.drawPixmap(fontPixmap, cursorLocation,  (-1 * letter.height) - letter.yoffset, letter.srcX, letter.srcY, letter.width, letter.height);
            cursorLocation += letter.width;
        }

        
        
//        Map<Integer, Integer> colorsMap = new HashMap<>();
//        
////        int c = Color.GREEN.toIntBits();
//        map.setColor(Color.GREEN);
//      // print out all the pixels
//        for (int i = 0; i < map.getWidth(); i++) {
//            for (int j = 0; j < map.getHeight(); j++) {
//
//                int color = map.getPixel(i,j);
//                if (colorsMap.get(color) != null) {
//                    colorsMap.put(color, colorsMap.get(color) + 1);
//                } else {
//                    colorsMap.put(color, 1);
//                }
//                
////                System.out.println("Color: " + map.getPixel(i, j));
//               int origColor = map.getPixel(i,j);
//                // 65025, 845
//                if (origColor == -1 || origColor == -1633771571 || origColor == -33685888) {
//                    System.out.println("Changing color");
//                    map.drawPixel(i,j);
//                }
//            }
//        }
//        
//        colorsMap.forEach((color, numColor) -> {
//            if (numColor > 10) {
//                System.out.println("Color: " + color + " : " + numColor);
//            }
//        });

        Texture textTexture = new Texture(map);
        TextureRegion textTextureRegion = new TextureRegion(textTexture);

        float scaleFactor = 0.1f;
        Decal nametag = Decal.newDecal(textTextureRegion.getRegionWidth() * scaleFactor, textTextureRegion.getRegionHeight() * scaleFactor, textTextureRegion, true);

        return nametag;
    }
}
