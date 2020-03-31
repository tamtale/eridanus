package com.week1.game.Model.Entities;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.week1.game.Model.Damage;
import com.week1.game.Model.OutputPath;
import com.week1.game.Model.Unit2StateAdapter;
import com.week1.game.Renderer.GameRenderable;
import com.week1.game.Renderer.RenderConfig;
import com.week1.game.Util3D;

import java.util.HashMap;
import java.util.Map;

import static com.week1.game.Model.StatsConfig.tempDamage;
import static com.week1.game.Model.StatsConfig.tempMinionRange;

public class Unit extends Damageable implements Damaging, GameRenderable, Clickable {
    private final int playerID;
    public OutputPath path;
    private Vector3 curNode;
    private Vector3 lastNode;
    private float distance;
    private float distanceTraveled;
    Unit2StateAdapter unit2StateAdapter;
    private Vector3 goal = new Vector3();
    private boolean close;
    private boolean selected;
    private float blockSpeed;
    private int turn = 0;
    private double hp;
    private Vector3 vel;
    private double maxHp;
    public int ID;
    public static double speed = 4;
    // 3D STUFF
    private static ModelBuilder BUILDER = new ModelBuilder();
    private Model model;
    private ModelInstance modelInstance;
    private Vector3 position = new Vector3();
    private Vector3 displayPosition = new Vector3();
    
    /*
     * Material to apply to a selected unit.
     */
    public static Material selectedMaterial = new Material() {{
        set(ColorAttribute.createDiffuse(Color.ORANGE));
    }};
    /*
     * Material to apply to a hovered unit.
     */
    public static Material hoveredMaterial = new Material() {{
        set(ColorAttribute.createDiffuse(Color.YELLOW));
    }};

    private Material originalMaterial;

    private static Map<Integer, Color> colorMap;
    private static Map<Integer, Model> modelMap;




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

    public static void setColorMapping(Map<Integer, Color> colorMapping) {
        colorMap = colorMapping;

        modelMap = new HashMap<Integer, Model>() {
            {
                colorMap.keySet().forEach(i ->
                        put(i, Util3D.ONLY.createBox(1, 1, 1, colorMap.get(i)))
                );
            }
        };
    }

    public void draw(Batch batch, float delta, boolean showAttackRadius) {
        if (delta == 0) {
            // Sync the state of units by not projecting anything
            displayPosition.set(position);
        } else {
            moveRender(delta);
        }
    }

    @Override
    public float getReward() {
        return 0;
    }


    @Override
    public <T> T accept(DamageableVisitor<T> visitor) {
        return visitor.acceptUnit(this);
    }

    public void step(float delta) {
        if (path != null) {
            if (path.getPath().size > 0) {
                if (distanceTraveled > distance) {
                    turn = 0;
                    float dx = path.get(0).x - position.x;
                    float dy = path.get(0).y - position.y;
                    int height = unit2StateAdapter.getHeight((int) position.x, (int) position.y);
                    int nxtHeight = unit2StateAdapter.getHeight((int) path.get(0).x, (int) path.get(0).y);
                    this.blockSpeed = 1f/unit2StateAdapter.getBlock((int) position.x, (int) position.y,
                            height).getCost(); //TODO: 3D
                    position.z = nxtHeight + 1;
                    this.distance = (float) Math.sqrt(Math.pow(dx, 2f) + Math.pow(dy, 2f));
                    double angle = Math.atan(dy / dx);
                    if (dx < 0) {
                        angle += Math.PI;
                    } else if (dy < 0) {
                        angle += 2 * Math.PI;
                    }
                    vel.x = blockSpeed * (float) speed * (float) Math.cos(angle);
                    vel.y = blockSpeed * (float) speed * (float) Math.sin(angle);
                    path.removeIndex(0);
                    this.distanceTraveled = 0;
                }
                move(delta);
                turn++;
            }
            if (path.getPath().size <= 0) {
                path = null;
                vel.x = 0;
                vel.y = 0;
            }
        }
        displayPosition.set(position); // Sync the unit's display to the next 'real' location
    }

    private void move(float delta) {
        Gdx.app.debug("move", "moving:" + position);
        position.set(position.x + (vel.x * delta), position.y + (vel.y * delta), position.z);
        modelInstance.transform.setToTranslation(position);
        this.distanceTraveled += Math.sqrt(Math.pow(vel.x * delta, 2) + Math.pow(vel.y * delta, 2));
    }

    private void moveRender(float delta) {
        displayPosition.x = displayPosition.x + (vel.x * delta);
        displayPosition.y = displayPosition.y + (vel.y * delta);
        modelInstance.transform.setToTranslation(displayPosition);
    }

    @Override
    public boolean takeDamage(Damaging attacker, double dmg, Damage.type damageType) {
        this.hp -= dmg;
        if (this.hp <= 0) {
            return true;
        } else {
            return false;
        }
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getZ() {
        return position.z;
    }


    public boolean isDead() {
        return this.hp <= 0;
    }

    @Override
    public boolean hasTargetInRange(Damageable victim) {
        return Math.sqrt(Math.pow(position.x - victim.getX(), 2) + Math.pow(position.y - victim.getY(), 2)) < tempMinionRange;
    }
    
    @Override
    public double getDamage() {
        return tempDamage;
    }

    @Override
    public int getPlayerId(){return playerID;}

    @Override
    public void getPos(Vector3 pos) {
        pos.set(position);
    }

    @Override
    public void getDisplayPos(Vector3 pos) {
        pos.set(displayPosition);
    }

    @Override
    public float getCurrentHealth() {
        return (float) this.hp;
    }

    @Override
    public float getMaxHealth() {
        return (float) this.maxHp;
    }

    public OutputPath getPath(){
        return path;
    }

    public float getVelocityX(){
        return vel.x;
    }

    public float getVelocityY(){
        return vel.y;
    }

    public void setPath(OutputPath path) {
        this.path = path;
        if (path.getPath().size >= 2) {
            path.removeIndex(0);
            float dx = path.get(0).x - this.position.x;
            float dy = path.get(0).y - this.position.y;
            double angle = Math.atan(dy / dx);
            if (dx < 0) {
                angle += Math.PI;
            } else if (dy < 0) {
                angle += 2 * Math.PI;
            }
            this.lastNode = new Vector3(this.position.x, this.position.y, 0);
            this.distance = (float) Math.sqrt(Math.pow(dx, 2f) + Math.pow(dy, 2f));
            vel.x = (float) speed * (float) Math.cos(angle);
            vel.y = (float) speed * (float) Math.sin(angle);
            this.distanceTraveled = 0;
            path.removeIndex(0);
        }
    }

    public void setGoal(Vector3 goal) {
        this.goal.set(goal);
    }

    public void setUnit2StateAdapter(Unit2StateAdapter unit2StateAdapter) {
        this.unit2StateAdapter = unit2StateAdapter;
    }

    @Override
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

    public void setMaterial(Material newMat, boolean changeToMat) {
        Material mat = modelInstance.materials.get(0);
        mat.clear();
        if (changeToMat) {
            mat.set(newMat);
        } else {
            mat.set(originalMaterial);
        }
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        setMaterial(selectedMaterial, selected);
    }

    @Override
    public void setHovered(boolean hovered) {
        if (this.selected) return;
        setMaterial(hoveredMaterial, hovered);
    }

    @Override
    public <T> T accept(ClickableVisitor<T> clickableVisitor) {
        return clickableVisitor.acceptUnit(this);
    }

    @Override
    public void render(RenderConfig config) {
        float delta = config.getDelta();
        if (delta != 0) {
            moveRender(delta);
        }
        config.getModelBatch().render(modelInstance, config.getEnv());
    }
}
