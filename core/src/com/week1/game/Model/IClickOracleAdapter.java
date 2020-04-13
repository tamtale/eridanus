package com.week1.game.Model;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.Entities.Clickable;
import com.week1.game.Model.Entities.Unit;
import com.week1.game.Networking.Messages.AMessage;
import com.week1.game.Renderer.RenderConfig;

/*
 * Adapter for the click oracle to interact with the rest of the system.
 */
public interface IClickOracleAdapter {
    /*
     * Returns the clickable at the given screen coordinates, closest to the camera.
     * Fills in intersection with the intersection coordinates, if any.
     */
    Clickable selectClickable(float screenX, float screenY, Vector3 intersection);
    boolean isPlayerAlive();
    Array<Unit> getUnitsInBox(Vector3 cornerA, Vector3 cornerB, RenderConfig renderConfig);
    int getGameStateHash();
    String getGameStateString();
    void sendMessage(AMessage msg);
    int getPlayerId();
    /* Inform the world camera to translate in the given direction, relative to the current view.*/
    void setTranslationDirection(Direction direction);
    /* Inform the world camera to rotate according to the given direction. */
    void setRotationDirection(RotationDirection direction);
    Camera getCamera();
}
