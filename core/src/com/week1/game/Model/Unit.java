package com.week1.game.Model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.week1.game.SteeringAgent;

import static com.week1.game.GameController.SCALE;

public class Unit extends Rectangle {
    public boolean clicked;
    public SteeringAgent agent;
    public Texture unselectedSkin, selectedSkin;
    private Pixmap unitPixmap, unitPixmap2;

    public Unit(float x, float y) {
        super(x, y, SCALE, SCALE);
        this.x = x;
        this.y = y;
        this.clicked = false;
        // TODO put Pixmap creation somewhere at game creation. only one is necessary per unit type.
        unitPixmap = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888);
        unitPixmap.setColor(Color.BLUE);
        unitPixmap.fill();
        this.unselectedSkin = new Texture(unitPixmap);

        unitPixmap2 = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888);
        unitPixmap2.setColor(Color.RED);
        unitPixmap2.fill();
        this.selectedSkin = new Texture(unitPixmap2);
    }

    public void step(float delta) {
        if (agent != null) {
            agent.update(delta);
        }
    }

    public Texture getSelectedSkin(){
        return selectedSkin;
    }

    public Texture getUnselectedSkin(){
        return unselectedSkin;
    }
}

