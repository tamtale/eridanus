package com.week1.game.Renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.Pair;

import java.util.List;

public class CrystalStage {
    Stage stage;
    CrystalDisplayWidget crystalWidget;
    int oldWidth, oldHeight;

    public CrystalStage() {
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        oldWidth = Gdx.graphics.getWidth();
        oldHeight = Gdx.graphics.getHeight();

        crystalWidget = new CrystalDisplayWidget();
        crystalWidget.setLblTxt("This is a test");
        Gdx.app.log("pjb3 - GameButtonsStage", "" + (Gdx.graphics.getWidth() * 256/800f) + " " + (Gdx.graphics.getWidth() * 256/800f));
        crystalWidget.setSize(Gdx.graphics.getWidth()/800f * 256,  Gdx.graphics.getHeight()/800f *128);
        Gdx.app.log("pjb3 - GameButtonsStage", "AND " + crystalWidget.getWidth() + " "+  crystalWidget.getHeight());
//        crystalWidget.setProportion(256/ GameController.VIRTUAL_WIDTH, 128 / GameController.VIRTUAL_WIDTH);
//        crystalWidget.setPosition( GameController.VIRTUAL_WIDTH - crystalWidget.getWidth(), GameController.VIRTUAL_HEIGHT - crystalWidget.getHeight());

        stage.addActor(crystalWidget);

    }
    public void resizeWidget(int width, int height) {
//        stage.getViewport().update(width, height);
        Gdx.app.log("pjb3 - GameButtonsStage", "resizing the window!!" + width + " " + height + " AND " + crystalWidget.getWidth() + " "+  crystalWidget.getHeight());

        crystalWidget.resetPosition((width - crystalWidget.getWidth()) - (width - oldWidth),
                (height - crystalWidget.getHeight()) - (height - oldHeight));
    }

    public void renderUI(List<Pair<String, Integer>> crystalCount) {
        crystalWidget.updateText(crystalCount);
        stage.draw();
    }
}
