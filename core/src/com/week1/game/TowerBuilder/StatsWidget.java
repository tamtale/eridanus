package com.week1.game.TowerBuilder;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.ArrayList;

public class StatsWidget extends Actor {
    private Label label;
    Label.LabelStyle tower1 = new Label.LabelStyle();
    Label.LabelStyle tower2= new Label.LabelStyle();
    Label.LabelStyle tower3= new Label.LabelStyle();
    Label.LabelStyle tower4= new Label.LabelStyle();
    Label.LabelStyle tower5= new Label.LabelStyle();
    Label.LabelStyle tower6= new Label.LabelStyle();
    private ArrayList<Label.LabelStyle> styles = new ArrayList<>();


    public StatsWidget() {
        TextureRegionDrawable td = new TextureRegionDrawable(new Texture("Tower1stats.png"));
        tower1.background = td;
        tower1.font = new BitmapFont();
        styles.add(tower1);

//        style.fontColor  = Color.BLACK;
        TextureRegionDrawable td2 = new TextureRegionDrawable(new Texture("Tower2stats.png"));
        tower2.background = td2;
        tower2.font = new BitmapFont();
        styles.add(tower2);

        TextureRegionDrawable td3 = new TextureRegionDrawable(new Texture("Tower3stats.png"));
        tower3.background = td3;
        tower3.font = new BitmapFont();
        styles.add(tower3);

        TextureRegionDrawable td4 = new TextureRegionDrawable(new Texture("Tower4stats.png"));
        tower4.background = td4;
        tower4.font = new BitmapFont();
        styles.add(tower4);

        TextureRegionDrawable td5 = new TextureRegionDrawable(new Texture("Tower5stats.png"));
        tower5.background = td5;
        tower5.font = new BitmapFont();
        styles.add(tower5);

        TextureRegionDrawable td6 = new TextureRegionDrawable(new Texture("Tower6stats.png"));
        tower6.background = td6;
        tower6.font = new BitmapFont();
        styles.add(tower6);

        label = new Label("",
                tower1);

    }

    @Override
    public void act(float delta) {
//        label.act(delta);
        label.setText("Stats");
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

     public void setLabelStyle(int towerNumber) {
        Label.LabelStyle currstyle = styles.get(towerNumber - 1);
        label.setStyle(currstyle);
     }


}
