package com.week1.game.Renderer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

public interface IRendererToEngineAdapter<TUnit> {
    void drawUnits(Batch batch);
}
