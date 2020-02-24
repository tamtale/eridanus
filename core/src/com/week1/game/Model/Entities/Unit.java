package com.week1.game.Model.Entities;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.AIMovement.SteeringAgent;
import com.week1.game.Model.Damage;
import com.week1.game.Model.OutputPath;
import javafx.scene.shape.Line;

import java.util.HashMap;
import java.util.Map;

import static com.week1.game.Model.StatsConfig.tempDamage;
import static com.week1.game.Model.StatsConfig.tempMinionRange;
import static com.week1.game.Renderer.TextureUtils.makeTexture;
import static java.lang.Math.abs;

public class Unit extends Rectangle implements Damageable, Damaging {
    private final int playerID;
    public OutputPath path;
    private Vector3 curNode;
    private Vector3 lastNode;
    private float distance;
    private float distanceTraveled;
    private Vector3 goal;
    private boolean close;

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }
    private int turn = 0;
    private double hp;
    private Vector3 vel;
    private float displayX, displayY;
    private double maxHp;
    private boolean clicked = false;
    public SteeringAgent agent;
    public int ID;
    public static double speed = 4;
    public  static int SIZE = 1;

    private Texture unselectedSkin;
    private static Texture selectedSkin = makeTexture(SIZE, SIZE, Color.YELLOW);
    private static Texture rangeCircle;


    private final static Map<Integer, Texture> colorMap = new HashMap<>();



    public Unit(float x, float y, Texture t, double hp, int playerID) {
        super(x, y, 1, 1); // TODO make the x and y the center points of it for getX() and getY() which is used in range calculations
        this.unselectedSkin = colorMap.get(playerID);
        this.playerID = playerID;
        this.hp = hp;
        this.maxHp = hp;
        this.displayX = x;
        this.displayY = y;
        this.vel = new Vector3(0, 0, 0);
    }

    public static void makeTextures() {
        Pixmap circlePixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
        circlePixmap.setBlending(Pixmap.Blending.None);
        circlePixmap.setColor(1, 1, 1, .5f);
        circlePixmap.drawCircle(50, 50, 50);
        rangeCircle = new Texture(circlePixmap);

        colorMap.put(0, makeTexture(SIZE, SIZE, Color.BLUE));
        colorMap.put(1, makeTexture(SIZE, SIZE, Color.RED));
        colorMap.put(2, makeTexture(SIZE, SIZE, Color.WHITE));
        colorMap.put(3, makeTexture(SIZE, SIZE, Color.PURPLE));
        colorMap.put(4, makeTexture(SIZE, SIZE, Color.PINK));
    }

    public void draw(Batch batch, float delta, boolean showAttackRadius) {
        if (delta == 0) {
            // Sync the state of units by not projecting anything
            displayX = x;
            displayY = y;
        } else {
            moveRender(delta);
        }

        if (showAttackRadius) {
            batch.draw(rangeCircle, displayX - ((float)tempMinionRange), displayY - ((float)tempMinionRange), (float)tempMinionRange * 2, (float)tempMinionRange * 2);
        }
        batch.draw(getSkin(), displayX - (SIZE / 2f), displayY - (SIZE / 2f), SIZE, SIZE);
        // TODO draw this in a UI rendering procedure
        drawHealthBar(batch, displayX, displayY, 0, SIZE, this.hp, this.maxHp);
    }

    public void step(float delta) {
        if (path != null) {
            if (path.getPath().size > 0) {
//                this.curNode = new Vector3(this.x, this.y, 0);
//                Line travelPath = new Line(lastNode.x, lastNode.y, curNode.x, curNode.y);
//                Rectangle nodeRect = new Rectangle(path.get(1).x, path.get(1).y, 1, 1);

//                boolean intersect = lineRect(lastNode.x, lastNode.y, curNode.x, curNode.y,
//                        path.get(1).x, path.get(1).y, 1, 1);
//                if (intersect){
                if (distanceTraveled > distance) {
                    turn = 0;
                    Gdx.app.setLogLevel(Application.LOG_NONE);
                    this.lastNode = new Vector3(this.x, this.y, 0);
//                    System.out.println("finX " + path.get(1).x + " finY " + path.get(1).y);
                    float dx = path.get(0).x - this.x;
                    float dy = path.get(0).y - this.y;
                    this.distance = (float) Math.sqrt(Math.pow(dx, 2f) + Math.pow(dy, 2f));
                    double angle = Math.atan(dy / dx);
                    if (dx < 0) {
                        angle += Math.PI;
                    } else if (dy < 0) {
                        angle += 2 * Math.PI;
                    }
                    vel.x = (float) speed * (float) Math.cos(angle);
                    vel.y = (float) speed * (float) Math.sin(angle);
                    path.removeIndex(0);
                    this.distanceTraveled = 0;
                }
                move(delta);
                turn++;

            }
            if (path.getPath().size <= 0) {
//                Gdx.app.log("Unit - pjb3", "Killing VELOCITY");
//                path.removeIndex(0);
                agent.setGoal(goal);
//                this.close = true;
                path = null;
                vel.x = 0;
                vel.y = 0;
            } else {
//                Gdx.app.log("Unit - pjb3", "Not Killing VELOCITY. Path len is " + path.getPath().size + " and the 0th is " + path.get(0));
            }
        }
//        Gdx.app.log("Unit - pjb3", "Checking velocity (" + vel.x + " " + vel.y + ")" + " goal (?,?) and pos (" + x + "," + y + ")");
        this.displayX = this.x; // Sync the unit's display to the next 'real' location
        this.displayY = this.y;

//        float dx = path.get(0).x - this.x;
//        float dy = path.get(0).y - this.y;
//
//        if (agent != null) {
//            agent.update(delta);
//        }
    }

    private void move(float delta) {
        this.setPosition(this.x + (vel.x * delta), this.y + (vel.y * delta));
        System.out.println("xdistTraveled " + vel.x * delta + "ydistTraveled " + vel.y * delta);
        this.distanceTraveled += Math.sqrt(Math.pow(vel.x * delta, 2) + Math.pow(vel.y * delta, 2));
        System.out.println("distance traveled" + distanceTraveled);

    }

    private void moveRender(float delta) {
        this.displayX = this.displayX + (vel.x * delta);
        this.displayY = this.displayY + (vel.y * delta);
    }

    public SteeringAgent getAgent(){ return agent;}
    public Texture getSelectedSkin(){
        return selectedSkin;
    }

    private Texture getSkin() {
        return clicked ? selectedSkin : unselectedSkin;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
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

    public OutputPath getPath(){
        return path;
    }

    public float getVelocityX(){
        return vel.x;
    }

    public float getVelocityY(){
        return vel.y;
    }

    public void setPath(OutputPath path) {
        this.path = path;
        path.removeIndex(0);
//        System.out.println("startX " + path.get(0).x);
//        System.out.println("startY " + path.get(0).y);
//        System.out.println("thisX " + this.x);
//        System.out.println("thisY " + this.y);
        float dx = path.get(0).x- this.x;
        float dy = path.get(0).y - this.y;
        double angle = Math.atan(dy/dx);
        if (dx < 0) {
            angle += Math.PI;
        } else if (dy < 0) {
            angle += 2 * Math.PI;
        }
        this.lastNode = new Vector3(this.x, this.y, 0);
        this.distance = (float) Math.sqrt(Math.pow(dx, 2f) + Math.pow(dy, 2f));
        vel.x = (float) speed * (float) Math.cos(angle);
        vel.y = (float) speed * (float) Math.sin(angle);
        System.out.println("vel.x " + vel.x + " vel.y " + vel.y);
        this.distanceTraveled = 0;
        path.removeIndex(0);
    }

    public float getDisplayX() {
        return displayX;
    }
    public float getDisplayY() {
        return displayY;
    }

    public void setGoal(Vector3 goal) {
        this.goal = goal;
    }

    @Override
    public String toString() {
        return "Unit{" +
                "playerID=" + playerID +
                ", turn=" + turn +
                ", hp=" + hp +
                ", vel=" + vel +
                ", maxHp=" + maxHp +
                ", ID=" + ID +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}

