package com.week1.game.Model.Systems;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Components.PositionComponent;
import com.week1.game.Model.Components.RenderNametagComponent;
import com.week1.game.Renderer.RenderConfig;

/*
 * System responsible for rendering decals in the game world.
 */
public class RenderNametagSystem implements ISystem {

    IntMap<RenderNametagNode> nametagNodes = new IntMap<>();

    // Used for internal calculations
    Vector3 lookAt = new Vector3();
    Vector3 tempPos = new Vector3();
    
    public void render(RenderConfig config) {
        DecalBatch decalBatch = config.getDecalBatch();
        for (RenderNametagNode node: nametagNodes.values()) {

            Vector3 loc = node.positionComponent.position;
            Decal nameTag = node.renderComponent.nametag();
            
            // Orient the decal
            Plane p = config.getCam().frustum.planes[0];
            Intersector.intersectLinePlane(
                    loc.x, loc.y, loc.z,
                    loc.x + p.normal.x, loc.y + p.normal.y, loc.z + p.normal.z,
                    p, lookAt);
            nameTag.lookAt(lookAt, config.getCam().up);

            // Set the position of the nametag
            tempPos.set(loc);
            tempPos.add(0,0,5f);
            nameTag.setPosition((tempPos));
            
            decalBatch.add(nameTag);
        }
        
        decalBatch.flush();
    }

    public void addNode(int id, RenderNametagComponent renderComponent, PositionComponent positionComponent) {
        nametagNodes.put(id, new RenderNametagNode(renderComponent, positionComponent));
    }
    
    @Override
    public void update(float delta) {

    }

    @Override
    public void remove(int entID) {
        nametagNodes.remove(entID);
    }

    static class RenderNametagNode {
        RenderNametagComponent renderComponent;
        PositionComponent positionComponent;
        public RenderNametagNode(RenderNametagComponent renderComponent, PositionComponent positionComponent) {
            this.renderComponent = renderComponent;
            this.positionComponent = positionComponent;
        }
    }
}
