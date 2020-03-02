package com.week1.game.Renderer;

/**
 * This class is passed from the renderer through to the game state when it is asking the
 * state to render. It contains options of what to render and the delta amount to move units.
 */
public class RenderConfig {
    private boolean showAttackRadius, showSpawnRadius;
    private float delta;


    public RenderConfig(boolean showAttackRadius, boolean showSpawnRadius, float delta) {
        this.showAttackRadius = showAttackRadius;
        this.showSpawnRadius = showSpawnRadius;
        this.delta = delta;
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
}
