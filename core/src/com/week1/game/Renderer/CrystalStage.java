package com.week1.game.Renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.week1.game.Tuple3;

import java.util.List;

public class CrystalStage {
    Stage stage;
    CrystalDisplayWidget crystalWidget;
    float oldWindowWidth, oldWindowHeight;
    float oldWidgetWidth, oldWidgetHeight;

    public CrystalStage() {
        stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        oldWindowWidth = Gdx.graphics.getWidth();
        oldWindowHeight = Gdx.graphics.getHeight();
        oldWidgetWidth = Gdx.graphics.getWidth()/800f * 256;
        oldWidgetHeight = Gdx.graphics.getHeight()/800f * 160;

        crystalWidget = new CrystalDisplayWidget();
        crystalWidget.setLblTxt("This is a test");
        Gdx.app.log("pjb3 - GameButtonsStage", "" + (Gdx.graphics.getWidth() * 256/800f) + " " + (Gdx.graphics.getWidth() * 256/800f));
        crystalWidget.setSize(oldWidgetWidth, oldWidgetHeight);
        Gdx.app.log("pjb3 - GameButtonsStage", "AND " + crystalWidget.getWidth() + " "+  crystalWidget.getHeight());
//        crystalWidget.setProportion(256/ GameController.VIRTUAL_WIDTH, 128 / GameController.VIRTUAL_WIDTH);
//        crystalWidget.setPosition( GameController.VIRTUAL_WIDTH - crystalWidget.getWidth(), GameController.VIRTUAL_HEIGHT - crystalWidget.getHeight());
        crystalWidget.resetPosition(Gdx.graphics.getWidth()/2f - oldWidgetWidth/2, Gdx.graphics.getHeight()/2f - oldWidgetHeight/2);
        stage.addActor(crystalWidget);

    }
    public void resizeWidget(int width, int height) {
//        crystalWidget.adjustScale(width, height);

    }

    public void renderUI(List<Tuple3<String, Integer, Color>> crystalCount) {
        crystalWidget.updateText(crystalCount);
        stage.draw();
    }
}
