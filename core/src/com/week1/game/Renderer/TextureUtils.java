package com.week1.game.Renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class TextureUtils {
    public static Texture makeTexture(int width, int height, Color color) {
        Pixmap map = new Pixmap(width, height, Pixmap.Format.RGB888);
        map.setColor(color);
        map.fill();
        Texture texture = new Texture(map);
        map.dispose();
        return texture;
    }
    
    public static Texture makeUnfilledRectangle(int width, int height, Color color) {
        Pixmap map = new Pixmap(width, height, Pixmap.Format.RGB888);

        
        map.setColor(Color.argb8888(155,155,0,0));
        map.fill();
        
//        map.setColor(color);
//        map.drawRectangle(0,0, width, height);
        
        Texture texture = new Texture(map);
        map.dispose();
        return texture;
    }
}
