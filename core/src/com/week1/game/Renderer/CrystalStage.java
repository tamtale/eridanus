package com.week1.game.Renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.week1.game.Tuple3;

import java.util.List;

public class CrystalStage {
    Stage stage;
    CrystalDisplayWidget crystalWidget;
    float oldWindowWidth, oldWindowHeight;
    float oldWidgetWidth, oldWidgetHeight;

    public CrystalStage() {
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
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

        stage.addActor(crystalWidget);

    }
    public void resizeWidget(int width, int height) {
//        stage.getViewport().update(width, height);
        Gdx.app.log("pjb3 - GameButtonsStage", "resizing the window!!" + width + " " +
                (Gdx.graphics.getWidth()/800f * oldWidgetWidth * oldWindowWidth * 1.0f / width) + " " +
                        oldWidgetWidth * 1.0f / width + " addsf " +
                height + " AND " + crystalWidget.getWidth() + " "+  crystalWidget.getHeight());

//        if (width * 1.0/height > 1) {
//            Gdx.app.log("pjb3 - GameButtonsStage", "UPP");
//            crystalWidget.setSize(Gdx.graphics.getWidth()/800f * oldWidgetWidth * oldWindowWidth * 1.0f / width,  oldWidgetHeight);
//        } else {
//            Gdx.app.log("pjb3 - GameButtonsStage", "downnnn");
//            crystalWidget.setSize(oldWidgetWidth,  Gdx.graphics.getHeight()/800f * (oldWidgetHeight * oldWindowHeight * 1.0f / height));
//        }

        crystalWidget.resetPosition((width - crystalWidget.getWidth()) - (width - oldWindowWidth),
                (height - crystalWidget.getHeight()) - (height - oldWindowHeight));
    }

    public void renderUI(List<Tuple3<String, Integer, Color>> crystalCount) {
        crystalWidget.updateText(crystalCount);
        stage.draw();
    }
}
