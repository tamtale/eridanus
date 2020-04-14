package com.week1.game.Model.Systems;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Components.PositionComponent;
import com.week1.game.Model.Components.TargetingComponent;
import com.week1.game.Model.World.GameWorld;
import com.week1.game.Pair;



/*
 * System responsible for creating fog of war
 */
public class FogSystem implements ISystem {
    
    private IntMap<Pair<PositionComponent, TargetingComponent>> seeingNodes = new IntMap<>();
    

    private boolean initialized = false;
    private GameWorld world;
    private int x, y;
    
    private boolean[][] oldVisible;
    boolean[][] newVisible;

    // used for internal calcuations
    private Vector3 loc = new Vector3();

    public FogSystem() {
    }
    
    public void init(GameWorld world) {
            
        this.world = world;
        
        int[] dims = world.getWorldDimensions();
        x = dims[0];
        y = dims[1];

        oldVisible = new boolean[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                oldVisible[i][j] = true;
            }
        }

        initialized = true;
    }

//    int i = 300;
    @Override
    public void update(float delta) {
        if (!initialized) {
            return; // don't try to do anything before initialization, or else null pointers will abound
        }
        newVisible = new boolean[x][y];
        
        
        // TODO: iterate through all registered seers (towers, units, bases) to mark blocks as visible
        seeingNodes.values().forEach((node) -> {
            Vector3 position = node.key.position;
            float range = node.value.range;
//            Vector3 position = new Vector3(i % (x -20), i / x, 0);
//            i++;
//            if (i >= x*y) {
//                i = 0;
//            }

            System.out.println("Lighting up near: " + position.x + ", " + position.y);

            safeSetNewVisible((int) position.x + 1, (int) position.y);
            safeSetNewVisible((int) position.x + 1, (int) position.y - 1);
            safeSetNewVisible((int) position.x + 1, (int) position.y + 1);
            safeSetNewVisible((int) position.x, (int) position.y);
            safeSetNewVisible((int) position.x, (int) position.y - 1);
            safeSetNewVisible((int) position.x, (int) position.y + 1);
            safeSetNewVisible((int) position.x - 1, (int) position.y);
            safeSetNewVisible((int) position.x - 1, (int) position.y - 1);
            safeSetNewVisible((int) position.x - 1, (int) position.y + 1);
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

    @Override
    public void remove(int entID) {
        seeingNodes.remove(entID);
    }
    
}
