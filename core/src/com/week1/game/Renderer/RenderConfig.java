package com.week1.game.Renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;

/**
 * This class is passed from the renderer through to the game state when it is asking the
 * state to render. It contains options of what to render and the delta amount to move units.
 */
public class RenderConfig {
    private boolean showAttackRadius, showSpawnRadius;
    private float delta;
    /* Batch used to draw health bar and other HUD stuff.*/
    private SpriteBatch batch = new SpriteBatch();
    /* ModelBatch used to draw 3D models.*/
    private ModelBatch modelBatch = new ModelBatch();

    private DecalBatch decalBatch;
    
    /* Camera used to view the world*/
    private Camera cam;
    
    
    /* Lighting and other global properties of the world.*/
    private Environment env;
    
//    public int zoomFactor = 0;


    public RenderConfig(boolean showAttackRadius, boolean showSpawnRadius, float delta, Camera cam, Environment env) {
        this.showAttackRadius = showAttackRadius;
        this.showSpawnRadius = showSpawnRadius;
        this.delta = delta;
        this.cam = cam;
        this.env = env;
        
        this.decalBatch = new DecalBatch(new CameraGroupStrategy(this.cam));
    }

    public void set(boolean showAttackRadius, boolean showSpawnRadius, float delta) {
        this.showAttackRadius = showAttackRadius;
        this.showSpawnRadius = showSpawnRadius;
        this.delta = delta;
    }

    public boolean isShowAttackRadius() {
        return showAttackRadius;
    }

    public boolean isShowSpawnRadius() {
        return showSpawnRadius;
    }

    public float getDelta() {
        return delta;
    }

    public SpriteBatch getBatch() {
        return batch;
    }
    

    public ModelBatch getModelBatch() {
        return modelBatch;
    }
    
    public DecalBatch getDecalBatch() {
        return decalBatch;
    }

    public Camera getCam() {
        return cam;
    }

    public Environment getEnv() {
        return env;
    }
    
    public void update() {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
    }
}
