package com.week1.game.Model.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.Model.Damage;
import static com.week1.game.Renderer.TextureUtils.makeTexture;

public class Crystal implements Damageable {

    public  static int SIZE = 1;

    private static Texture selectedSkin = makeTexture(SIZE, SIZE, Color.CYAN);
    private Vector3 position = new Vector3();

    public Crystal(float x, float y) {
        position.set(x, y, 0);
    }

    public void draw(Batch batch) {
        batch.draw(selectedSkin, position.x - (SIZE / 2f), position.y - (SIZE / 2f), SIZE, SIZE);
    }

    @Override
    public float getReward() {
        return 1;
    }

    @Override
    public <T> T accept(DamageableVisitor<T> visitor) {
        return visitor.acceptCrystal(this);
    }

    public Texture getSelectedSkin(){
        return selectedSkin;
    }

    @Override
    public boolean takeDamage(double dmg, Damage.type damageType) {
        return true;
    }

    @Override
    public float getX() {
        return position.x;
    }

    @Override
    public float getY() {
        return position.y;
    }


    public boolean isDead() {
        return false;
    }

    @Override
    public int getPlayerId() {
        return -1;
    }
}

