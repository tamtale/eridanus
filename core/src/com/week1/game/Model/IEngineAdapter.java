package com.week1.game.Model;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.Model.Entities.Unit;
import com.week1.game.Networking.Messages.AMessage;
import com.week1.game.Renderer.RenderConfig;

public interface IEngineAdapter {
    void sendMessage(AMessage msg);
    void setDefaultLocation(Vector3 location);
    void endGame(int winOrLoss); // loss is 0, win is 1
    void gameOver();
    /* Inform the rest of the system where the center of the map is.*/
    void setCenter(Vector3 center);
    RenderConfig getRenderConfig();
}
