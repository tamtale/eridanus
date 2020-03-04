package com.week1.game.Renderer;

import com.week1.game.Model.SpawnInfo;

/*
 * Adapter from the renderer to the system.
 */
public interface IRendererAdapter {
    void setSelectedSpawnState(SpawnInfo type);

    /*
     * Render the rest of the system given the config.
     */
    void renderSystem(RenderConfig renderConfig);

    double getPlayerMana(int playerId);

    void restartGame();

    String getHostAddr();
    int getPlayerId();
    String getClientAddr();
}
