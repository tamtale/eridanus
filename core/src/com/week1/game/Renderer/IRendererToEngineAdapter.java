package com.week1.game.Renderer;

import com.badlogic.gdx.maps.tiled.TiledMap;

public interface IRendererToEngineAdapter<TUnit> {
    // This will ask the engine to go through and send everything to be drawn to the renderer.
    void render(RenderConfig renderConfig);

    double getPlayerMana(int playerId);
}
