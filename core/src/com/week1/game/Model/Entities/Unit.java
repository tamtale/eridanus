package com.week1.game.Model.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
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
import static java.lang.Math.abs;

public class Unit implements Damageable, Damaging, RenderableProvider, Clickable {
    private final int playerID;
    public OutputPath path;
    private int turn = 0;
    private double hp;
    private Vector3 vel;
    private double maxHp;
    private boolean clicked = false;
    public SteeringAgent agent;
    public int ID;
    public static double speed = 5;
    public  static int SIZE = 1;
    // 3D STUFF
    private static ModelBuilder BUILDER = new ModelBuilder();
    private Model model;
    private ModelInstance modelInstance;
    private Vector3 position = new Vector3();
    private Vector3 displayPosition = new Vector3();

    /*
     * Material to apply to a selected unit.
     */
    private static Material selectedMaterial = new Material() {{
        set(ColorAttribute.createDiffuse(Color.ORANGE));
    }};
    private Material originalMaterial;

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


    public Unit(float x, float y, float z, double hp, int playerID) {
        this.position.set(x, y, z);
        this.displayPosition.set(x, y, z);
        this.playerID = playerID;
        this.hp = hp;
        this.maxHp = hp;
        this.vel = new Vector3(0, 0, 0);
        this.model = modelMap.get(playerID);
        this.modelInstance = new ModelInstance(model);
        modelInstance.transform.setTranslation(x, y, z);
        this.originalMaterial = model.materials.get(0);
    }

    public void draw(Batch batch, float delta, boolean showAttackRadius) {
        if (delta == 0) {
            // Sync the state of units by not projecting anything
            displayPosition.set(position);
        } else {
            moveRender(delta);
        }
//
//        if (showAttackRadius) {
//            batch.draw(rangeCircle, displayX - ((float)tempMinionRange), displayY - ((float)tempMinionRange), (float)tempMinionRange * 2, (float)tempMinionRange * 2);
//        }
//        batch.draw(getSkin(), displayX - (SIZE / 2f), displayY - (SIZE / 2f), SIZE, SIZE);
//        // TODO draw this in a UI rendering procedure
//        drawHealthBar(batch, displayX, displayY, 0, SIZE, this.hp, this.maxHp);
    }

    public void step(float delta) {
        if (path != null) {
            if (path.getPath().size != 1) {
                if ((abs((int) position.x - (int) path.get(0).x) <= 1 &&
                        abs((int) position.y - (int) path.get(0).y) <= 1)) {
                    turn = 0;
                    float dx = path.get(1).x - position.x;
                    float dy = path.get(1).y - position.y;
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
                vel.x = 0;
                vel.y = 0;
            }
        }
        displayPosition.set(position); // Sync the unit's display to the next 'real' location
    }

    private void move(float delta) {
        position.set(position.x + (vel.x * delta), position.y + (vel.y * delta), position.z);
        modelInstance.transform.setToTranslation(position);
    }

    private void moveRender(float delta) {
        displayPosition.x = displayPosition.x + (vel.x * delta);
        displayPosition.y = displayPosition.y + (vel.y * delta);
        modelInstance.transform.setToTranslation(displayPosition);
    }

    public SteeringAgent getAgent(){ return agent;}

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
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

    @Override
    public float getX() {
        return position.x;
    }

    @Override
    public float getY() {
        return position.y;
    }

    public float getZ() {
        return position.z;
    }

    public void collide() {
        Vector3 linVel = agent.getLinearVelocity();
        position.set(position.x - 2 * linVel.x, position.y - 2 * linVel.y, position.z);
        agent.setLinearVelocity(new Vector3(0, 0, 0));
        agent.setSteeringBehavior(null);
    }


    public boolean isDead() {
        return this.hp <= 0;
    }

    @Override
    public boolean hasTargetInRange(Damageable victim) {
        return Math.sqrt(Math.pow(position.x - victim.getX(), 2) + Math.pow(position.y - victim.getY(), 2)) < tempMinionRange;
//        return true; // TODO
    }
    
    @Override
    public double getDamage() {
        return tempDamage;
    }

    @Override
    public int getPlayerId(){return playerID;}
    
    public boolean contains(float x, float y) {
        return (position.x - (SIZE / 2f) < x) && (x < position.x + (SIZE / 2f)) &&
                (position.y - (SIZE / 2f) < y) && (y < position.y + (SIZE / 2f));
                
    }

    public void setPath(OutputPath path) {
        this.path = path;
        float dx = path.get(0).x - position.x;
        float dy = path.get(0).y - position.y;
        vel.x = dx * .333f;
        vel.y = dy * .333f;
    }

    public float getDisplayX() {
        return displayPosition.x;
    }
    public float getDisplayY() {
        return displayPosition.y;
    }

    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        modelInstance.getRenderables(renderables, pool);
    }

    public String toString() {
        return "Unit{" +
                "playerID=" + playerID +
                ", turn=" + turn +
                ", hp=" + hp +
                ", vel=" + vel +
                ", maxHp=" + maxHp +
                ", ID=" + ID +
                ", x=" + position.x +
                ", y=" + position.y +
                '}';
    }

    /*
     * BoundingBox to be reused for intersection calculation.
     */
    private static BoundingBox box = new BoundingBox();
    @Override
    public boolean intersects(Ray ray, Vector3 intersection) {
        modelInstance.calculateBoundingBox(box);
        box.mul(modelInstance.transform);
        return Intersector.intersectRayBounds(ray, box, intersection);
    }

    @Override
    public void setSelected(boolean selected) {
        Material mat = modelInstance.materials.get(0);
        mat.clear();
        if (selected) {
          mat.set(selectedMaterial);
        } else {
          mat.set(originalMaterial);
        }
    }

    @Override
    public <T> T accept(ClickableVisitor<T> clickableVisitor) {
        return clickableVisitor.acceptUnit(this);
    }
}

