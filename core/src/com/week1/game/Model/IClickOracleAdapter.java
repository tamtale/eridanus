package com.week1.game.Model;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.Entities.Building;
import com.week1.game.Model.Entities.Clickable;
import com.week1.game.Model.Entities.Unit;
import com.week1.game.Networking.Messages.AMessage;

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
    Array<Unit> getUnitsInBox(Vector3 cornerA, Vector3 cornerB);
    Array<Building> getBuildings();
    int getGameStateHash();
    String getGameStateString();
    void sendMessage(AMessage msg);
    int getPlayerId();
    void unproject(Vector3 projected);
    void setTranslationDirection(Direction direction);
    Camera getCamera();
}
