package com.week1.game.Renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.week1.game.Tuple3;

import java.util.List;

public class CrystalDisplayWidget extends Actor {
//    private float width_p, height_p;

    private Label label;

    public CrystalDisplayWidget() {
        BitmapFont font = new BitmapFont();
        font.getData().markupEnabled = true;
        Label.LabelStyle panelstyle = new Label.LabelStyle(font, null);
        TextureRegionDrawable td2 = new TextureRegionDrawable(new Texture("stats_panel.png"));
        panelstyle.background = td2;
        panelstyle.font = new BitmapFont();

        label = new Label("INIT",
                panelstyle);
        label.setAlignment(Align.center);
        label.setFontScale(1.3f);
        font.getData().markupEnabled = true;
    }

    @Override
    public void act(float delta) {
//        label.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        label.draw(batch, parentAlpha);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        label.setPosition(x, y);
    }

//    public void setProportion(float width_p, float height_p) {
//        this.width_p = width_p;
//        this.height_p = height_p;
//        super.setSize(Gdx.graphics.getWidth() * width_p, Gdx.graphics.getWidth() * height_p);
//        label.setSize(Gdx.graphics.getWidth()* width_p, Gdx.graphics.getWidth() * height_p);
//    }

    public void setLblTxt(String statsStr) {
        this.label.setText(statsStr);
    }

    public void resetPosition(float x, float y) {
        setPosition(x, y);
//        setPosition(0,0);


    }

    @Override
    public float getWidth() {
//        return Gdx.graphics.getWidth()  * width_p;
        return label.getWidth();
    }

    @Override
    public float getHeight() {
//        return Gdx.graphics.getHeight() * height_p;
        return label.getHeight();
    }

    public void updateText(List<Tuple3<String, Integer, Color>> crystalCount) {
        StringBuilder newText = new StringBuilder();
        newText.append("Crystal Tracker");
        Gdx.app.log("", "" + crystalCount + " " + crystalCount.size());
        for (int i = 0; i < crystalCount.size(); i++ ) {
            newText.append("[" + crystalCount.get(i)._3.toString() + "]").append(crystalCount.get(i)._1).append(": ").append("[#000000]").append(crystalCount.get(i)._2);
            if (i != crystalCount.size() - 1) {
                newText.append("\n");
            }
        }
        setLblTxt(newText.toString());
    }

    public void setSize(float x, float y) {
        super.setSize(x, y);
        label.setSize(x, y);
    }
}
