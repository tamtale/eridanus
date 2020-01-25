package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.Networking.Messages.CreateMinionMessage;
import com.week1.game.Networking.Messages.MoveMinionMessage;
import com.week1.game.Networking.Messages.Game.CreateMinionMessage;
import com.week1.game.Networking.Messages.Game.CreateTowerMessage;

public class ClickOracle extends InputAdapter {

    private Vector3 touchPos = new Vector3();
    private IClickOracleToRendererAdapter rendererAdapter;
    private IClickOracleToEngineAdapter engineAdapter;
    private Unit selected;
    private IClickOracleToNetworkAdapter networkAdapter;

    public ClickOracle(IClickOracleToRendererAdapter rendererAdapter, 
                       IClickOracleToEngineAdapter engineAdapter,
                       IClickOracleToNetworkAdapter networkAdapter) {
        this.rendererAdapter = rendererAdapter;
        this.engineAdapter = engineAdapter;
        this.networkAdapter = networkAdapter;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        touchPos.set(screenX, screenY, 0);
        rendererAdapter.unproject(touchPos);

        if (button == Input.Buttons.LEFT) {

            // Create tower with left click and numberkey down
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
                Gdx.app.log("lji1 - ClickOracle", "Spawn basic tower.");
                networkAdapter.sendMessage(new CreateTowerMessage(touchPos.x, touchPos.y, TowerType.BASIC, networkAdapter.getPlayerId()));
            } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
                Gdx.app.log("lji1 - ClickOracle", "Spawn sniper tower.");
                networkAdapter.sendMessage(new CreateTowerMessage(touchPos.x, touchPos.y, TowerType.SNIPER, networkAdapter.getPlayerId()));
            } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
                Gdx.app.log("lji1 - ClickOracle", "Spawn tank tower.");
                networkAdapter.sendMessage(new CreateTowerMessage(touchPos.x, touchPos.y, TowerType.TANK, networkAdapter.getPlayerId()));
            } else {

                Unit unit = engineAdapter.selectUnit(touchPos);
                if (unit == null) {
                    Gdx.app.log("ttl4 - ClickOracle", "nothing selected!");
                    networkAdapter.sendMessage(new CreateMinionMessage(touchPos.x, touchPos.y, 69, networkAdapter.getPlayerId()));
                } else {
                    Gdx.app.log("ttl4 - ClickOracle", "selected selected!");
                    select(unit);
                }
            }
            return true;
        }
        // Right click
        if (selected != null && button == Input.Buttons.RIGHT) {
            // TODO: steering agent behavior
            networkAdapter.sendMessage(new MoveMinionMessage(touchPos.x, touchPos.y, 69, 420, selected.ID));
            return true;

        } else {
            return false;
        }
    }

    private void select(Unit unit) {
        unselect();
        selected = unit;
        if (unit != null) {
            unit.clicked = true;
        }
    }
    private void unselect() {
        if (selected != null) {
            selected.clicked = false;
        }
        selected = null;
    }
}
