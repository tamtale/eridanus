package com.week1.game.Model;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.Model.Entities.Unit;
import com.week1.game.Networking.Messages.AMessage;

public interface IEngineAdapter {
    void sendMessage(AMessage msg);
    void sendToModelBatch(RenderableProvider provider);
    void setDefaultLocation(Vector3 location);
    void endGame(int winOrLoss); // loss is 0, win is 1
    void gameOver();
}
