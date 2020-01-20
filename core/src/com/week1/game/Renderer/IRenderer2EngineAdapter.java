package com.week1.game.Renderer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

public interface IRenderer2EngineAdapter<TUnit> {
    void drawUnits(Batch batch);
}
