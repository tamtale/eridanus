package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.week1.game.AIMovement.SteeringAgent;

import java.util.HashMap;
import java.util.Map;


public class Unit extends Rectangle implements Damageable {
    private final int playerID;
    private float hp;
    public boolean clicked = false;
    public SteeringAgent agent;
    public int ID;
    private static Pixmap unitPixmap2 = new Pixmap(64, 64, Pixmap.Format.RGB888){{
        setColor(Color.YELLOW);
        fill();
    }};
    private static Texture selectedSkin = new Texture(unitPixmap2){{
        unitPixmap2.dispose();
    }};

    private static Texture makeTexture(Color color) {
        Pixmap map = new Pixmap(1, 1, Pixmap.Format.RGB888);
        map.setColor(color);
        map.fill();
        Texture texture = new Texture(map);
        map.dispose();
        return texture;
    }

    private final static Map<Integer, Texture> colorMap = new HashMap<Integer, Texture>() {
        {
            put(0, makeTexture(Color.BLUE));
            put(1, makeTexture(Color.RED));
            put(2, makeTexture(Color.WHITE));
            put(3, makeTexture(Color.PURPLE));
            put(4, makeTexture(Color.PINK));
        }
    };

    private Texture unselectedSkin;

    public Unit(float x, float y, Texture t, float hp, int playerID) {
        super(x, y, 1, 1);
        this.unselectedSkin = colorMap.get(playerID);
        this.playerID = playerID;
        this.hp = hp;
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
//        Gdx.app.log("lji1 - getUnselectedSkin", "unselectedSkin: " + unselectedSkin);
        return unselectedSkin;
    }
    public int getPlayerID(){return playerID;}

    @Override
    public boolean takeDamage(float dmg, int damageType) {
        this.hp -= dmg;
        if (this.hp <= 0) {
            return true;
            // TODO again, might need to do more than returning true.
        } else {
            return false;
        }
    }
}

