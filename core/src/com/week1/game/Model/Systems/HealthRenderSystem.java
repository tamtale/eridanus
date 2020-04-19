package com.week1.game.Model.Systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Components.HealthComponent;
import com.week1.game.Model.Components.OwnedComponent;
import com.week1.game.Model.Components.PositionComponent;
import com.week1.game.Model.Components.VisibleComponent;
import com.week1.game.Model.Entities.Unit;
import com.week1.game.Model.Entities.UnitLoader;
import com.week1.game.Renderer.RenderConfig;

import static com.week1.game.Renderer.TextureUtils.makeTexture;

/*
 * System responsible for rendering healthbars.
 */
public class HealthRenderSystem implements ISystem {

    static class HealthRenderNode {
        public PositionComponent position;
        public HealthComponent health;
        public OwnedComponent owned;
        public VisibleComponent visible;
        public float maxWidth;
        public Decal hpBar;
        public HealthRenderNode(PositionComponent position,
                                HealthComponent health,
                                OwnedComponent owned,
                                VisibleComponent visible) {
            this.position = position;
            this.health = health;
            this.owned = owned;
            this.visible = visible;
            this.maxWidth = hpBarWidthFactor * (float)Math.log(health.maxHealth);
        }
    }

    public IntMap<HealthRenderNode> nodes = new IntMap<>();
    // Texture regions for each player.
    public IntMap<TextureRegion> healthBarRegions = new IntMap<>();
    {{
        healthBarRegions.put(-1, new TextureRegion(makeTexture(1, 1, Color.WHITE)));
    }}

    @Override
    public void update(float delta) {

    }

    @Override
    public void remove(int entID) {
        nodes.remove(entID);
    }

    public void render(RenderConfig config) {
        for (HealthRenderNode node: nodes.values()) {
            if (node != null && node.visible.visible()) { // only render the hp bar if it is visible
                renderHealthbar(config, node);
            }
        }
        config.getDecalBatch().flush();
    }

    private TextureRegion getTextureRegion(int playerID) {
        if (healthBarRegions.get(playerID) == null) {
            healthBarRegions.put(playerID, new TextureRegion(makeTexture(1, 1, UnitLoader.NAMES_TO_COLORS.get(Unit.factionMap.get(playerID)))));
        }
        return healthBarRegions.get(playerID);
    }

    // Used for rendering calculations.
    private Vector3 barPosition = new Vector3();
    private Vector3 lookAt = new Vector3();
    private static float hpBarWidthFactor = 0.3f;

    private void renderHealthbar(RenderConfig config, HealthRenderNode node) {
        Vector3 unitPosition = node.position.position;
        DecalBatch batch = config.getDecalBatch();
        // Orient the decal
        Plane p = config.getCam().frustum.planes[0];
        Intersector.intersectLinePlane(
            unitPosition.x, unitPosition.y, unitPosition.z,
            unitPosition.x + p.normal.x, unitPosition.y + p.normal.y, unitPosition.z + p.normal.z,
            p, lookAt);
        if (node.hpBar == null) {
            node.hpBar = Decal.newDecal(1, 0.15f, getTextureRegion(node.owned.playerID));
        }
        Decal hpBar = node.hpBar;
        hpBar.lookAt(lookAt, config.getCam().up);
        float newWidth = node.maxWidth * node.health.curHealth / node.health.maxHealth;
        // Set the position of the decal
        hpBar.setWidth(newWidth);
        barPosition.set(unitPosition);
        barPosition.add(0, -(node.maxWidth - newWidth)/2,1.5f);
        hpBar.setPosition(barPosition);
        batch.add(hpBar);
    }

    public void addNode(int id, PositionComponent positionComponent, HealthComponent healthComponent, OwnedComponent ownedComponent, VisibleComponent visibleComponent) {
        nodes.put(id, new HealthRenderNode(positionComponent, healthComponent, ownedComponent, visibleComponent));
    }

}
