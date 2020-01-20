package com.week1.game.Model;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class ClickOracle extends InputAdapter {

    private Vector3 touchPos;
    private IClickOracle2RendererAdapter rendererAdapter;
    private IClickOracle2EngineAdapter engineAdapter;
    private Unit selected;

    public ClickOracle(IClickOracle2RendererAdapter rendererAdapter, IClickOracle2EngineAdapter engineAdapter) {
        this.rendererAdapter = rendererAdapter;
        this.engineAdapter = engineAdapter;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        touchPos.set(screenX, screenY, 0);
        rendererAdapter.unproject(touchPos);

        if (button == Input.Buttons.LEFT) {
            selected = engineAdapter.selectUnit(touchPos);
            if (selected == null) {
            }

            select(engineAdapter.spawn(touchPos));
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
        unit.clicked = true;
    }
    private void unselect() {
        if (selected != null) {
            selected.clicked = false;
        }
        selected = null;
    }
}
