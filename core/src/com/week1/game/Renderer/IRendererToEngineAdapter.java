package com.week1.game.Renderer;

import com.badlogic.gdx.maps.tiled.TiledMap;

public interface IRendererToEngineAdapter<TUnit> {
    // This will ask the engine to go through and send everything to be drawn to the renderer.
    void render();

    // TODO get rid of this
    TiledMap getMap();
    double getPlayerMana(int playerId);

}
