package com.week1.game.Model.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.AIMovement.SteeringAgent;
import com.week1.game.Model.Damage;
import com.week1.game.Model.OutputPath;

import java.util.HashMap;
import java.util.Map;

import static com.week1.game.Model.StatsConfig.tempDamage;
import static com.week1.game.Model.StatsConfig.tempMinionRange;
import static com.week1.game.Renderer.TextureUtils.makeTexture;

public class Unit extends Rectangle implements Damageable, Damaging {
    private final int playerID;
    public OutputPath path;
    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }
    private int turn = 0;
    private double hp;
    private Vector3 vel;
    private double maxHp;
    public boolean clicked = false;
    public SteeringAgent agent;
    public int ID;
    public static double speed = 15;
    public  static int SIZE = 1;
    
    private static Texture selectedSkin = makeTexture(SIZE, SIZE, Color.YELLOW);

    public Unit(float x, float y, Texture t, double hp, int playerID) {
        super(x, y, 1, 1); // TODO make the x and y the center points of it for getX() and getY() which is used in range calculations
        this.unselectedSkin = colorMap.get(playerID);
        this.playerID = playerID;
        this.hp = hp;
        this.maxHp = hp;
        this.vel = new Vector3(0, 0, 0);
    }

    public void draw(Batch batch) {
        batch.draw(getSkin(), this.x - (SIZE / 2f), this.y - (SIZE / 2f), SIZE, SIZE);
        // TODO draw this in a UI rendering procedure
        drawHealthBar(batch, this.x, this.y, 0, SIZE, this.hp, this.maxHp);
    }


    private final static Map<Integer, Texture> colorMap = new HashMap<Integer, Texture>() {
        {
            put(0, makeTexture(SIZE, SIZE, Color.BLUE));
            put(1, makeTexture(SIZE, SIZE, Color.RED));
            put(2, makeTexture(SIZE, SIZE, Color.WHITE));
            put(3, makeTexture(SIZE, SIZE, Color.PURPLE));
            put(4, makeTexture(SIZE, SIZE, Color.PINK));
        }
    };

    private Texture unselectedSkin;


    public void step(float delta) {
//        if (path != null) {
//            System.out.println(path.get(0));
//            System.out.println(this.x);
//            System.out.println(this.y);
//            if (this.x > path.get(0).x && this.y > path.get(0).y) {
//                Gdx.app.log("Unit - wab2", "Updating goal");
//                agent.setGoal(path.get(1));
//                path.setPath(path.getPath().removeIndex(0));
//            }
//        }
        if (path != null) {
            if (path.getPath().size != 1) {
                if (((int) this.x == (int) path.get(0).x &&
                    (int) this.y == (int) path.get(0).y) || turn == 6) {
                    turn = 0;
                    float dx = path.get(1).x - (int) this.x;
                    float dy = path.get(1).y - (int) this.y;
                    double angle = Math.atan(dy/dx);
                    if (dx < 0) {
                        angle += Math.PI;
                    } else if (dy < 0) {
                        angle += 2 * Math.PI;
                    }

                    vel.x = (float) speed * (float) Math.cos(angle);
                    vel.y = (float) speed * (float) Math.sin(angle);
                    path.removeIndex(0);
                }
                move(delta);
                turn ++;
//                if (turn == 0){
//                    System.out.println(this.x + " " + this.y);
//                    System.out.println(path.getPath());
//                    path.removeIndex(0);
//                    turn = 4;
//                } else {
//                    turn--;
//                }

            }
            if(path.getPath().size == 0) {
                vel.x = 0;
                vel.y = 0;
            }
        }
//        float dx = path.get(0).x - this.x;
//        float dy = path.get(0).y - this.y;
//
//        if (agent != null) {
//            agent.update(delta);
//        }
    }

    private void move(float delta) {
        this.setPosition(this.x + (vel.x * delta), this.y + (vel.y * delta));
    }

    public SteeringAgent getAgent(){ return agent;}
    public Texture getSelectedSkin(){
        return selectedSkin;
    }

    private Texture getSkin() {
        return clicked ? selectedSkin : unselectedSkin;
    }
    
    @Override
    public boolean takeDamage(double dmg, Damage.type damageType) {
        this.hp -= dmg;
        if (this.hp <= 0) {
            return true;
            // TODO again, might need to do more than returning true.
        } else {
            return false;
        }
    }
    
    
    public boolean isDead() {
        return this.hp <= 0;
    }

    @Override
    public boolean hasTargetInRange(Damageable victim) {
        return Math.sqrt(Math.pow(this.x - victim.getX(), 2) + Math.pow(this.y - victim.getY(), 2)) < tempMinionRange;
//        return true; // TODO
    }
    
    @Override
    public double getDamage() {
        return tempDamage;
    }

    @Override
    public int getPlayerId(){return playerID;}
    
    @Override
    public boolean contains(float x, float y) {
        return (this.x - (SIZE / 2f) < x) && (x < this.x + (SIZE / 2f)) &&
                (this.y - (SIZE / 2f) < y) && (y < this.y + (SIZE / 2f));
                
    }

    public void setPath(OutputPath path) {
        this.path = path;
        float dx = path.get(0).x - this.x;
        float dy = path.get(0).y - this.y;
        vel.x = dx * .333f;
        vel.y = dy * .333f;
    }
}

