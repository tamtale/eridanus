package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.Networking.Messages.CreateMinionMessage;

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
            Unit unit = engineAdapter.selectUnit(touchPos);
            if (unit == null) {
                Gdx.app.log("ttl4 - ClickOracle", "nothing selected!");
                networkAdapter.sendMessage(new CreateMinionMessage(touchPos.x, touchPos.y, 69, 420));
            } else {
                Gdx.app.log("ttl4 - ClickOracle", "selected selected!");
                select(unit);
            }
            return true;
        }
        // Right click
        if (selected != null) {
            // TODO: steering agent behavior
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
