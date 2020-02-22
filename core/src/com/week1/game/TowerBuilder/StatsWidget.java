package com.week1.game.TowerBuilder;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class StatsWidget extends Actor {
    private Label label;


    public StatsWidget() {

        Label.LabelStyle panelstyle = new Label.LabelStyle();
        TextureRegionDrawable td2 = new TextureRegionDrawable(new Texture("stats_panel.png"));
        panelstyle.background = td2;
        panelstyle.font = new BitmapFont();

        label = new Label("",
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

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        label.setSize(width, height);
    }

     public void setLblTxt(Integer hp, Integer atk, Integer range, Integer price) {
        this.label.setText("HP: " + hp +"\n Atk: " + atk + " \n Range: " + range + "\n Price: " + price);
     }


}
