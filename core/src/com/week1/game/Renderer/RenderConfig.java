package com.week1.game.Renderer;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

/**
 * This class is passed from the renderer through to the game state when it is asking the
 * state to render. It contains options of what to render and the delta amount to move units.
 */
public class RenderConfig {
    private boolean showAttackRadius, showSpawnRadius;
    private float delta;
    /* Batch used to draw health bar and other HUD stuff.*/
    private Batch batch = new SpriteBatch();
    /* ModelBatch used to draw 3D models.*/
    private ModelBatch modelBatch = new ModelBatch();
    /* Camera used to view the world*/
    private Camera cam;
    /* Lighting and other global properties of the world.*/
    private Environment env;


    public RenderConfig(boolean showAttackRadius, boolean showSpawnRadius, float delta, Camera cam, Environment env) {
        this.showAttackRadius = showAttackRadius;
        this.showSpawnRadius = showSpawnRadius;
        this.delta = delta;
        this.cam = cam;
        this.env = env;
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

    public Batch getBatch() {
        return batch;
    }

    public ModelBatch getModelBatch() {
        return modelBatch;
    }

    public Camera getCam() {
        return cam;
    }

    public Environment getEnv() {
        return env;
    }
}
