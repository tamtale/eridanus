package com.week1.game.Renderer;

import com.badlogic.gdx.graphics.Color;
import com.week1.game.Model.SpawnInfo;
import com.week1.game.Tuple3;

import java.util.List;

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

    void setFog(boolean enabled);

    int getPlayerId();

    String getTowerName(int slotNum);

    int getTowerCost(int slotNum);

    int getUnitCost();

    List<Tuple3<String, Integer, Color>> getCrystalCount();
}
