package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class BuilderInputProcessor extends InputAdapter {

    private TowerBuilderScreen screen;

    public BuilderInputProcessor(TowerBuilderScreen screen) {
        this.screen = screen;
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {

        if (screen.isBuildMode()) {
            if (screen.isAddMode() ) {
                if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) & !Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)){
                    screen.addBlock(screenX, screenY);
                }
            } else if (screen.isChangeMode()) {
                screen.changeBlock(screenX, screenY);
            } else if (screen.isDelMode()) {
                screen.deleteBlock(screenX, screenY);
            }
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (screen.isAddMode()) {
            screen.highlightBlock(screenX, screenY);

            return true;
        } else if (screen.isChangeMode() || screen.isDelMode()) {
            screen.highlightTowerBlock(screenX, screenY);
        }
        return false;
    }


}
