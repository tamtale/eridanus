package com.week1.game.Model.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.week1.game.AIMovement.SteeringAgent;
import com.week1.game.Model.Damage;
import com.week1.game.Model.OutputPath;
import com.week1.game.Util3D;

import java.util.HashMap;
import java.util.Map;

import static com.week1.game.Model.StatsConfig.tempDamage;
import static com.week1.game.Model.StatsConfig.tempMinionRange;
import static com.week1.game.Renderer.TextureUtils.makeTexture;
import static java.lang.Math.abs;

public class Unit extends Rectangle implements Damageable, Damaging, RenderableProvider {
    private final int playerID;
    public OutputPath path;
    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }
    private int turn = 0;
    private double hp;
    private Vector3 vel;
    private float displayX, displayY;
    private double maxHp;
    public boolean clicked = false;
    public SteeringAgent agent;
    public int ID;
    public static double speed = 5;
    public  static int SIZE = 1;

    private Texture unselectedSkin;
    private static Texture selectedSkin = makeTexture(SIZE, SIZE, Color.YELLOW);
    private static Texture rangeCircle;

    // 3D STUFF
    private static ModelBuilder BUILDER = new ModelBuilder();
    private Model model;
    private ModelInstance modelInstance;

    static {
        Pixmap circlePixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
        circlePixmap.setBlending(Pixmap.Blending.None);
        circlePixmap.setColor(1, 1, 1, .5f);
        circlePixmap.drawCircle(50, 50, 50);
        rangeCircle = new Texture(circlePixmap);
    }
    private final static Map<Integer, Texture> textureMap = new HashMap<Integer, Texture>() {
        {
            put(0, makeTexture(SIZE, SIZE, Color.BLUE));
            put(1, makeTexture(SIZE, SIZE, Color.RED));
            put(2, makeTexture(SIZE, SIZE, Color.WHITE));
            put(3, makeTexture(SIZE, SIZE, Color.PURPLE));
            put(4, makeTexture(SIZE, SIZE, Color.PINK));
        }
    };

    private final static Map<Integer, Color> colorMap = new HashMap<Integer, Color>() {
        {
            put(0, Color.BLUE);
            put(1, Color.RED);
            put(2, Color.WHITE);
            put(3, Color.PURPLE);
            put(4, Color.PINK);
        }
    };

    private final static Map<Integer, Model> modelMap = new HashMap<Integer, Model>() {
        {
            colorMap.keySet().forEach(i ->
                    put(i, Util3D.ONLY.createBox(1, 1, 1, colorMap.get(i)))
            );
        }
    };


    public Unit(float x, float y, Texture t, double hp, int playerID) {
        super(x, y, 1, 1); // TODO make the x and y the center points of it for getX() and getY() which is used in range calculations
        this.unselectedSkin = textureMap.get(playerID);
        this.playerID = playerID;
        this.hp = hp;
        this.maxHp = hp;
        this.displayX = x;
        this.displayY = y;
        this.vel = new Vector3(0, 0, 0);
        this.model = modelMap.get(playerID);
        this.modelInstance = new ModelInstance(model);
        modelInstance.transform.setTranslation(x, y, 1);
    }

    public void draw(Batch batch, float delta, boolean showAttackRadius) {
        if (delta == 0) {
            // Sync the state of units by not projecting anything
            displayX = x;
            displayY = y;
        } else {
            moveRender(delta);
        }

        if (showAttackRadius) {
            batch.draw(rangeCircle, displayX - ((float)tempMinionRange), displayY - ((float)tempMinionRange), (float)tempMinionRange * 2, (float)tempMinionRange * 2);
        }
        batch.draw(getSkin(), displayX - (SIZE / 2f), displayY - (SIZE / 2f), SIZE, SIZE);
        // TODO draw this in a UI rendering procedure
        drawHealthBar(batch, displayX, displayY, 0, SIZE, this.hp, this.maxHp);
    }

    public void step(float delta) {
        if (path != null) {
            if (path.getPath().size != 1) {
                if ((abs((int) this.x - (int) path.get(0).x) <= 1 &&
                        abs((int) this.y - (int) path.get(0).y) <= 1)) {
                    turn = 0;
                    float dx = path.get(1).x - (int) this.x;
                    float dy = path.get(1).y - (int) this.y;
                    double angle = Math.atan(dy/dx);
                    if (dx < 0) {
                        angle += Math.PI;
                    } else if (dy < 0) {
                        angle += 2 * Math.PI;
                    }

                    vel.x = (float) speed * (float) Math.cos(angle);
                    vel.y = (float) speed * (float) Math.sin(angle);
                    path.removeIndex(0);
                }
                move(delta);
                turn ++;
            }
            if (path.getPath().size <= 1) {
//                Gdx.app.log("Unit - pjb3", "Killing VELOCITY");
                vel.x = 0;
                vel.y = 0;
            } else {
//                Gdx.app.log("Unit - pjb3", "Not Killing VELOCITY. Path len is " + path.getPath().size + " and the 0th is " + path.get(0));
            }
        }
//        Gdx.app.log("Unit - pjb3", "Checking velocity (" + vel.x + " " + vel.y + ")" + " goal (?,?) and pos (" + x + "," + y + ")");
        this.displayX = this.x; // Sync the unit's display to the next 'real' location
        this.displayY = this.y;

//        float dx = path.get(0).x - this.x;
//        float dy = path.get(0).y - this.y;
//
//        if (agent != null) {
//            agent.update(delta);
//        }
    }

    private void move(float delta) {
        this.setPosition(this.x + (vel.x * delta), this.y + (vel.y * delta));
        modelInstance.transform.setToTranslation(x, y, 1); // TODO perhaps use observer pattern here?
    }

    private void moveRender(float delta) {
        this.displayX = this.displayX + (vel.x * delta);
        this.displayY = this.displayY + (vel.y * delta);
        modelInstance.transform.setToTranslation(x, y, 1); // TODO perhaps use observer pattern here?
    }

    public SteeringAgent getAgent(){ return agent;}
    public Texture getSelectedSkin(){
        return selectedSkin;
    }

    private Texture getSkin() {
        return clicked ? selectedSkin : unselectedSkin;
    }
    
    @Override
    public boolean takeDamage(double dmg, Damage.type damageType) {
        this.hp -= dmg;
        if (this.hp <= 0) {
            return true;
            // TODO again, might need to do more than returning true.
        } else {
            return false;
        }
    }
    
    
    public boolean isDead() {
        return this.hp <= 0;
    }

    @Override
    public boolean hasTargetInRange(Damageable victim) {
        return Math.sqrt(Math.pow(this.x - victim.getX(), 2) + Math.pow(this.y - victim.getY(), 2)) < tempMinionRange;
//        return true; // TODO
    }
    
    @Override
    public double getDamage() {
        return tempDamage;
    }

    @Override
    public int getPlayerId(){return playerID;}
    
    @Override
    public boolean contains(float x, float y) {
        return (this.x - (SIZE / 2f) < x) && (x < this.x + (SIZE / 2f)) &&
                (this.y - (SIZE / 2f) < y) && (y < this.y + (SIZE / 2f));
                
    }

    public void setPath(OutputPath path) {
        this.path = path;
        float dx = path.get(0).x - this.x;
        float dy = path.get(0).y - this.y;
        vel.x = dx * .333f;
        vel.y = dy * .333f;
    }

    public float getDisplayX() {
        return displayX;
    }
    public float getDisplayY() {
        return displayY;
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        modelInstance.getRenderables(renderables, pool);
    }
}

