package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.week1.game.AIMovement.SteeringAgent;

import java.util.HashMap;
import java.util.Map;

import static com.week1.game.GameController.SCALE;

public class Unit extends Rectangle implements Damageable {
    private final int playerID;
    private float hp;
    public boolean clicked = false;
    public SteeringAgent agent;
    public int ID;
    
    
    private static Pixmap unitPixmap2 = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888){{
        setColor(Color.YELLOW);
        fill();
    }};
    private static Texture selectedSkin = new Texture(unitPixmap2){{
        unitPixmap2.dispose();
    }};


    private final static Map<Integer, Texture> colorMap = new HashMap<Integer, Texture>() {{

        Pixmap blueMap = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888){{
            setColor(Color.BLUE);
            fill();
        }};
        Texture blueTexture = new Texture(blueMap){{ blueMap.dispose(); }};
        this.put(0, blueTexture);

        Pixmap redMap = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888){{
            setColor(Color.RED);
            fill();
        }};
        Texture redTexture = new Texture(redMap){{ redMap.dispose(); }};
        this.put(1, redTexture);

        Pixmap whiteMap = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888){{
            setColor(Color.WHITE);
            fill();
        }};
        Texture whiteTexture = new Texture(whiteMap){{ whiteMap.dispose(); }};
        this.put(2, whiteTexture);
        
        Pixmap purpleMap = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888){{
            setColor(Color.PURPLE);
            fill();
        }};
        Texture purpleTexture = new Texture(purpleMap){{ purpleMap.dispose(); }};
        this.put(3, purpleTexture);

        Pixmap pinkMap = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888){{
            setColor(Color.PINK);
            fill();
        }};
        Texture pinkTexture = new Texture(pinkMap){{ pinkMap.dispose(); }};
        this.put(4, pinkTexture);
    }};
        
        
        
    private Texture unselectedSkin;

    public Unit(float x, float y, Texture t, float hp, int playerID) {
        super(x, y, SCALE, SCALE);
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

