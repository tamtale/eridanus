package com.week1.game.Renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class CrystalDisplayWidget extends Actor {
    private float width_p, height_p;

    private Label label;

    public CrystalDisplayWidget() {
        Label.LabelStyle panelstyle = new Label.LabelStyle();
        TextureRegionDrawable td2 = new TextureRegionDrawable(new Texture("stats_panel.png"));
        panelstyle.background = td2;
        panelstyle.font = new BitmapFont();

        label = new Label("TETOMG",
                panelstyle);
        label.setAlignment(Align.center);
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

    public void setProportion(float width_p, float height_p) {
        this.width_p = width_p;
        this.height_p = height_p;
        super.setSize(Gdx.graphics.getWidth() * width_p, Gdx.graphics.getWidth() * height_p);
        label.setSize(Gdx.graphics.getWidth()* width_p, Gdx.graphics.getWidth() * height_p);
    }

    public void setLblTxt(String statsStr) {
        this.label.setText(statsStr);
    }

    public void resize() {

    }

    public void resetPosition(int width, int height) {
        setPosition(Gdx.graphics.getWidth() - getWidth(), Gdx.graphics.getHeight() - getHeight());
        Gdx.app.log("pjb3", "" + (Gdx.graphics.getWidth() - getWidth()) + " " + (Gdx.graphics.getHeight() - getHeight()) + " and the width p is " + width_p);

    }

    @Override
    public float getWidth() {
        return Gdx.graphics.getWidth()  * width_p;
    }

    @Override
    public float getHeight() {
        return Gdx.graphics.getHeight() * height_p;
    }
}
