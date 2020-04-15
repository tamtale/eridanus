package com.week1.game.Model;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.Model.Entities.Unit;
import com.week1.game.Model.Events.SelectionEvent;
import com.week1.game.Model.Systems.Subscriber;
import com.week1.game.Networking.Messages.AMessage;
import com.week1.game.Renderer.RenderConfig;

import java.util.List;

public interface IEngineAdapter {
    void sendMessage(AMessage msg);
    void setDefaultLocation(Vector3 location);
    void endGame(int winOrLoss); // loss is 0, win is 1
    void gameOver();
    /* Inform the rest of the system where the center of the map is.*/
    void setCenter(Vector3 center);
    
    /* 
        Allows the GameEngine to request that the camera zoom in or out.
        Used to zoom out pre-game, so that the camera doesn't start too
        close to the map.
     */
    void zoom(float amount);
    
    List<PlayerInfo> getPlayerInfo();

    /* Given an internal subscriber of selection events, register to any external publishers (i.e. ClickOracle.) */
    void subscribeSelection(Subscriber<SelectionEvent> subscriber);
}
