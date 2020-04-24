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
        crystalWidget.setLblTxt("Initial Text");
        crystalWidget.setSize(oldWidgetWidth, oldWidgetHeight);
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
