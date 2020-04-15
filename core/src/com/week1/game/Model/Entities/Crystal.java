package com.week1.game.Model.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.week1.game.Model.Components.*;
import com.week1.game.Model.Initializer;

/*
 * Crystals are damageable entities that give mana any time it's hit by a unit.
 */
public class Crystal implements Clickable {
    public int ID;
   
    private PositionComponent positionComponent;
    private HealthComponent healthComponent;
    private ManaRewardComponent manaRewardComponent;
    private RenderComponent renderComponent;
    private VisibleComponent visibleComponent;
    private boolean selected;

    /*
     * Material to apply to a selected unit.
     */
    private static Material selectedMaterial = new Material() {{
        set(ColorAttribute.createDiffuse(Color.ORANGE));
    }};

    /*
     * Material to apply to a hovered unit.
     */
    private static Material hoveredMaterial = new Material() {{
        set(ColorAttribute.createDiffuse(Color.YELLOW));
    }};

    private static Material originalMaterial = Initializer.crystal.materials.get(0);


    public Crystal(PositionComponent positionComponent, HealthComponent healthComponent, ManaRewardComponent manaRewardComponent, RenderComponent renderComponent, VisibleComponent visibleComponent, int ID) {
        this.positionComponent = positionComponent;
        this.healthComponent = healthComponent;
        this.manaRewardComponent = manaRewardComponent;
        this.renderComponent = renderComponent;
        this.visibleComponent = visibleComponent;
        this.ID = ID;
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

    public int getPlayerId() {
        return -1;
    }

    public PositionComponent getPositionComponent() {
        return positionComponent;
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
        return visibleComponent.visible;
    }

    @Override
    public <T> T accept(ClickableVisitor<T> clickableVisitor) {
        return clickableVisitor.acceptCrystal(this);
    }

}

