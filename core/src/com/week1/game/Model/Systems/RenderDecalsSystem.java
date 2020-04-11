package com.week1.game.Model.Systems;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Components.HealthComponent;
import com.week1.game.Model.Components.PositionComponent;
import com.week1.game.Model.Components.RenderComponent;
import com.week1.game.Model.Components.RenderDecalComponent;
import com.week1.game.Renderer.RenderConfig;

/*
 * System responsible for rendering decals in the game world.
 */
public class RenderDecalsSystem implements ISystem {

    IntMap<RenderNametagNode> nametagNodes = new IntMap<>();
//    IntMap<RenderDecalNode> hpbarNodes = new IntMap<>();

    // Used for internal calculations
    Vector3 lookAt = new Vector3();
    Vector3 tempPos = new Vector3();
    
    public void render(RenderConfig config) {
        
        // TODO: also render the hp bars
        
        DecalBatch decalBatch = config.getDecalBatch();
        for (RenderNametagNode node: nametagNodes.values()) {

            Vector3 loc = node.positionComponent.position;
            Decal nameTag = node.renderComponent.decal;
            
            // Orient the decal
            Plane p = config.getCam().frustum.planes[0];
            Intersector.intersectLinePlane(
                    loc.x, loc.y, loc.z,
                    loc.x + p.normal.x, loc.y + p.normal.y, loc.z + p.normal.z,
                    p, lookAt);
            nameTag.lookAt(lookAt, config.getCam().up);

            // Set the position of the nametag
            tempPos.set(loc);
            tempPos.add(0,0,3.5f);
            nameTag.setPosition((tempPos));
            
            decalBatch.add(nameTag);
        }
        
        decalBatch.flush();
    }

    public void addNametagNode(int id, RenderDecalComponent renderComponent, PositionComponent positionComponent) {
        nametagNodes.put(id, new RenderNametagNode(renderComponent, positionComponent));
    }
    
    public void addHpbarNode(int id, RenderDecalComponent renderComponent, PositionComponent positionComponent, HealthComponent healthComponent) {
//        hpbarNodes.put(id, new RenderDecalNode(renderComponent, positionComponent));
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void remove(int entID) {
        nametagNodes.remove(entID);
//        hpbarNodes.remove(entID);
    }

    static class RenderNametagNode {
        RenderDecalComponent renderComponent;
        PositionComponent positionComponent;
        public RenderNametagNode(RenderDecalComponent renderComponent, PositionComponent positionComponent) {
            this.renderComponent = renderComponent;
            this.positionComponent = positionComponent;
        }
    }
}
