package com.week1.game.Model.Systems;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Components.OwnedComponent;
import com.week1.game.Model.Components.PositionComponent;
import com.week1.game.Model.Components.TargetingComponent;
import com.week1.game.Model.Components.VisibleComponent;
import com.week1.game.Model.World.GameWorld;
import com.week1.game.Pair;
import com.week1.game.Tuple3;

import static com.week1.game.Model.StatsConfig.ENABLE_FOG;


/*
 * System responsible for creating fog of war
 */
public class FogSystem implements ISystem {
    
    private IntMap<Pair<PositionComponent, TargetingComponent>> seeingNodes = new IntMap<>();
    private IntMap<Pair<PositionComponent, VisibleComponent>> seenNodes = new IntMap<>();

    private boolean initialized = false;
    private GameWorld world;
    private int localPlayer;
    private int x, y;
    
    private boolean[][] oldVisible;
    boolean[][] newVisible;

    public FogSystem() {
    }
    
    public void init(GameWorld world, int localPlayer) {
            
        this.world = world;
        this.localPlayer = localPlayer;
        
        int[] dims = world.getWorldDimensions();
        x = dims[0];
        y = dims[1];

        oldVisible = new boolean[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                oldVisible[i][j] = true;
            }
        }


        if (!ENABLE_FOG) { // everything should be made visible
            for (int i = 0; i < x; i++) {
                for (int j = 0; j < y; j++) {
                    world.markForUnhide(i, j);
                }
            }
        }
        
        initialized = true;
    }

    @Override
    public void update(float delta) {
        if (!initialized || !ENABLE_FOG) {
            return; // don't try to do anything before initialization, or else null pointers will abound
        }
        newVisible = new boolean[x][y];
        
        
        seeingNodes.values().forEach((node) -> {

            Vector3 position = node.key.position;
//            float range = node._2.range; // TODO: range based on actual range?
            int range = 10;

            for (int i = (int)position.x - range; i < position.x + range; i++) {
                for (int j = (int)position.y - range; j < position.y + range; j++) {
                    float dist  = (float)Math.sqrt(Math.pow(i - position.x, 2) + Math.pow(j - position.y, 2));
                    if (dist <= range) {
                        safeSetNewVisible(i, j);
                    }
                }
            }
        });


        // For any block visibility that has changed, update that block
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                    if (newVisible[i][j] != oldVisible[i][j]) {
                        if (newVisible[i][j]) {
                            world.markForUnhide(i, j);
                        } else {
                            world.markForHide(i, j);
                        }
                }
            }
        }
        
        // Also update columns with recently placed blocks
        Array<Pair<Integer, Integer>> recentLocs = world.pollRecentlyChangedLocations();
        for (int i = 0; i < recentLocs.size; i++) {
            int x = recentLocs.get(i).key;
            int y = recentLocs.get(i).value;
            if (newVisible[x][y]) {
                world.markForUnhide(x, y);
            } else {
                world.markForHide(x, y);
            }
        }
        
        // update visiblity for things like units and hp bars
        seenNodes.forEach((seenNode) ->  {
            PositionComponent p = seenNode.value.key;
            VisibleComponent v = seenNode.value.value;
            
            v.setVisible(newVisible[(int)p.position.x][(int)p.position.y]);
        });
        
        oldVisible = newVisible;
    }
    
    public void safeSetNewVisible(int i, int j) {
        if (0 <= i && i < x && 0 <= j && j < y) {
            newVisible[i][j] = true;
        }
    }
    
    public void addSeer(int entId, PositionComponent positionComponent, TargetingComponent targetingComponent) {
       seeingNodes.put(entId, new Pair<>(positionComponent, targetingComponent));
    }
    
    public void addSeen(int entId, PositionComponent positionComponent, VisibleComponent visibleComponent) {
        seenNodes.put(entId, new Pair<>(positionComponent, visibleComponent));
    }

    @Override
    public void remove(int entID) {
        seeingNodes.remove(entID);
        seenNodes.remove(entID);
    }
    
}
