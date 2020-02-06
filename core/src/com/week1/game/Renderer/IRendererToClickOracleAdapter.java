package com.week1.game.Renderer;

import com.week1.game.Model.SpawnInfo;

public interface IRendererToClickOracleAdapter {
    void render();
    void setSelectedSpawnState(SpawnInfo type);
}
