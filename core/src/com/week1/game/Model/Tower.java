package com.week1.game.Model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import static com.week1.game.GameController.SCALE;

public class Tower {
    public float x, y;
    private Texture skin;
    private int playerID;
    private TowerType type;

    public Tower(Texture t, float x, float y, TowerType towerType, int playerID) {
        skin = t;
        this.x = x;
        this.y = y;
        this.type = towerType;
        this.playerID = playerID;
    }

    private static Pixmap blueMap = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888){{
        setColor(Color.BLUE);
        fill();
    }};
    private static Texture blue = new Texture(blueMap){{
        blueMap.dispose();
    }};
    
    public Texture getSkin() {
        return blue;
//        return skin;
    }
}
