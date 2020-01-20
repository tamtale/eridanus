package com.week1.game.Renderer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

public interface IRendererToEngineAdapter<TUnit> {
    // This will ask the engine to go through and send everything to be drawn to the renderer.
    void render();
}
