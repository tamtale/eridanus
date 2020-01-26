package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.week1.game.AIMovement.SteeringAgent;

import static com.week1.game.GameController.SCALE;

public class Unit extends Rectangle {
    private final int playerID;
    public boolean clicked = false;
    public SteeringAgent agent;
    public int ID;
//    private static Pixmap unitPixmap = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888){{
//       setColor(Color.BLUE);
//       fill();
//    }};
    private static Pixmap unitPixmap2 = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888){{
        setColor(Color.YELLOW);
        fill();
    }};
//    private static Texture unselectedSkin = new Texture(unitPixmap){{
//        unitPixmap.dispose();
//    }};
    private static Texture selectedSkin = new Texture(unitPixmap2){{
        unitPixmap2.dispose();
    }};

            Pixmap blueMap = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888){{
            setColor(Color.BLUE);
            fill();
        }};
        Texture blueTexture = new Texture(blueMap){{ blueMap.dispose(); }};
    private Texture unselectedSkin;

    public Unit(float x, float y, Texture t, int playerID) {
        super(x, y, SCALE, SCALE);
        this.unselectedSkin = blueTexture;
        this.playerID = playerID;
    }

    public void step(float delta) {
        if (agent != null) {
            agent.update(delta);
        }
    }

    public SteeringAgent getAgent(){ return agent;}
    public Texture getSelectedSkin(){
        return selectedSkin;
    }

    public Texture getUnselectedSkin(){
        Gdx.app.log("lji1 - getUnselectedSkin", "unselectedSkin: " + unselectedSkin);
        return unselectedSkin;
    }
    public int getPlayerID(){return playerID;}
}

