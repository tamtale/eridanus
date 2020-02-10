package com.week1.game.Renderer;

public class RenderConfig {
    private boolean showAttackRadius, showSpawnRadius;


    public RenderConfig(boolean showAttackRadius, boolean showSpawnRadius) {
        this.showAttackRadius = showAttackRadius;
        this.showSpawnRadius = showSpawnRadius;
    }

    public boolean isShowAttackRadius() {
        return showAttackRadius;
    }

    public boolean isShowSpawnRadius() {
        return showSpawnRadius;
    }
}
