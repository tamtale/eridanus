package com.week1.game.Model;

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
    private static Pixmap unitPixmap = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888){{
       setColor(Color.BLUE);
       fill();
    }};
    private static Pixmap unitPixmap2 = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888){{
        setColor(Color.RED);
        fill();
    }};
    private static Texture unselectedSkin = new Texture(unitPixmap){{
        unitPixmap.dispose();
    }};
    private static Texture selectedSkin = new Texture(unitPixmap2){{
        unitPixmap2.dispose();
    }};

    public Unit(float x, float y, int playerID) {
        super(x, y, SCALE, SCALE);
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
        return unselectedSkin;
    }
    public int getPlayerID(){return playerID;}
}

