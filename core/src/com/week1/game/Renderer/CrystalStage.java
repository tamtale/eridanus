package com.week1.game.Renderer;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class CrystalStage {
    Stage stage;
    CrystalDisplayWidget crystalWidget;

    public CrystalStage() {
        stage = new Stage(new ScreenViewport());

        crystalWidget = new CrystalDisplayWidget();
        crystalWidget.setLblTxt("This is a test");
//        crystalWidget.setProportion(256/ GameController.VIRTUAL_WIDTH, 128 / GameController.VIRTUAL_WIDTH);
//        crystalWidget.setPosition( GameController.VIRTUAL_WIDTH - crystalWidget.getWidth(), GameController.VIRTUAL_HEIGHT - crystalWidget.getHeight());

    }
}
