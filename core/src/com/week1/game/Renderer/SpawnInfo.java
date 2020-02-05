package com.week1.game.Renderer;

public class SpawnInfo {

    public enum SpawnType {
        UNIT,
        TOWER1,
        TOWER2,
        TOWER3,
    }

    private SpawnType type;

    public SpawnInfo(SpawnType type) {
        this.type = type;
    }

    public SpawnType getType() {
        return type;
    }
}
