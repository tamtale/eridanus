package com.week1.game.Model.Entities;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.week1.game.Model.Components.*;
import com.week1.game.Model.OutputPath;

import java.util.HashMap;
import java.util.Map;

public class Unit implements Clickable {
    private PositionComponent positionComponent;
    private VelocityComponent velocityComponent;
    private PathComponent pathComponent;
    private RenderComponent renderComponent;
    private OwnedComponent ownedComponent;
    private HealthComponent healthComponent;
    private VisibleComponent visibleComponent;
    private DamagingComponent damagingComponent;

    private boolean selected;
    private int turn = 0;
    public int ID;
    public static double speed = 4;
    // 3D STUFF
    private Model model;

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

    public static Map<Integer, String> factionMap;
    public static Map<Integer, Model> modelMap;

    public Unit(
        PositionComponent positionComponent,
        VelocityComponent velocityComponent,
        PathComponent pathComponent,
        RenderComponent renderComponent,
        OwnedComponent ownedComponent,
        HealthComponent healthComponent,
        VisibleComponent visibleComponent,
        DamagingComponent damagingComponent // The unit needs to be able to modify its own damage
    ) {
        this.positionComponent = positionComponent;
        this.velocityComponent = velocityComponent;
        this.pathComponent = pathComponent;
        this.renderComponent = renderComponent;
        this.ownedComponent = ownedComponent;
        this.healthComponent = healthComponent;
        this.model = modelMap.get(ownedComponent.playerID);
        this.originalMaterial = model.materials.get(0);
        this.visibleComponent = visibleComponent;
        this.damagingComponent = damagingComponent;
    }

    public static void setColorMapping(Map<Integer, String> colorMapping) {
        factionMap = colorMapping;

        modelMap = new HashMap<Integer, Model>() {
            {
                //TODO -- is this ok???
                factionMap.keySet().forEach(i ->
                        put(i, UnitLoader.NAMES_TO_FACTIONS.get(colorMapping.get(i)).getModel())
                );
            }
        };
    }

    public float getX() {
        return positionComponent.position.x;
    }

    public float getY() {
        return positionComponent.position.y;
    }

    public float getZ() {
        return positionComponent.position.z;
    }

    public PositionComponent getPositionComponent() {
        return positionComponent;
    }

    public int getPlayerId() {return ownedComponent.playerID;}

    public void setPath(OutputPath path) {
        this.pathComponent.path = path;
        if (path.getPath().size >= 2) {
            path.removeIndex(0);
            float dx = path.get(0).x - this.positionComponent.position.x;
            float dy = path.get(0).y - this.positionComponent.position.y;
            double angle = Math.atan(dy / dx);
            if (dx < 0) {
                angle += Math.PI;
            } else if (dy < 0) {
                angle += 2 * Math.PI;
            }
            velocityComponent.velocity.x = (float) speed * (float) Math.cos(angle);
            velocityComponent.velocity.y = (float) speed * (float) Math.sin(angle);
            path.removeIndex(0);
        }
    }

    public void setGoal(Vector3 goal) {
        this.pathComponent.goal.set(goal);
    }

    @Override
    public String toString() {
        return "Unit{" +
                " playerID=" + ownedComponent.playerID +
                ", turn=" + turn +
                ", hp=" + healthComponent.curHealth +
                ", vel=" + velocityComponent.velocity +
                ", maxHp=" + healthComponent.maxHealth +
                ", ID=" + ID +
                ", x=" + positionComponent.position.x +
                ", y=" + positionComponent.position.y +
                '}';
    }

    /*
     * BoundingBox to be reused for intersection calculation.
     */
    private static BoundingBox box = new BoundingBox();
    @Override
    public boolean intersects(Ray ray, Vector3 intersection) {
        renderComponent.modelInstance.calculateBoundingBox(box);
        box.mul(renderComponent.modelInstance.transform);
        return Intersector.intersectRayBounds(ray, box, intersection);
    }

    public void setMaterial(Material newMat, boolean changeToMat) {
        Material mat = renderComponent.modelInstance.materials.get(0);
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
    public boolean visible() {
        return visibleComponent.visible();
    }

    @Override
    public <T> T accept(ClickableVisitor<T> clickableVisitor) {
        return clickableVisitor.acceptUnit(this);
    }


    public void setHealth(float newHealth) {
        float healthDiff = newHealth - healthComponent.maxHealth;
        healthComponent.curHealth += healthDiff;
        healthComponent.maxHealth = newHealth;
//        Gdx.app.log("pjb3- Unit", "New max " + healthComponent.maxHealth + " cur health max " + healthComponent.curHealth);
    }

    public void setDamage(float newDmg) {
//        Gdx.app.log("pjb3- Unit", "New damage"  + newDmg);
        damagingComponent.baseDamage = newDmg;
    }
}