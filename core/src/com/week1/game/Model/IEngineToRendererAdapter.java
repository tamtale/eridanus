package com.week1.game.Model;

import com.badlogic.gdx.graphics.Texture;

public interface IEngineToRendererAdapter {

    //void pleaseRerender();
    void startBatch();
    void draw(Texture texture, float x, float y);
    void endBatch();

}
