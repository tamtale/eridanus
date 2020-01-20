package com.week1.game.Renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.GameState;
import com.week1.game.Model.Unit;
import com.week1.game.SteeringAgent;

public class Renderer {

    public static int SCALE = 8; // 8 pixels per unit.
    public static int SPEED = 8;
    private Batch batch;
    private OrthographicCamera camera;
    private Array<Unit> units;
    private Pixmap unitPixmap;
    private Pixmap unitPixmap2;
    private Texture unitTexture;
    private Texture unitTexture2;
    private Vector3 touchPos = new Vector3();
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private Unit selected;

    public void create(GameState state) {
        map = new TmxMapLoader().load("testmap.tmx");
        camera = new OrthographicCamera();
        renderer = new OrthogonalTiledMapRenderer(map, 1f / SCALE);
        camera.setToOrtho(false, 256, 256);
        camera.update();
    }

    public void step(float delta) {
        for(Unit unit: units) {
            //System.out.println("from step " + agent.getSteeringOutput().linear);
            unit.step(delta);
        }

    }

    public void render(GameState state) {
        float diff = Gdx.graphics.getDeltaTime();
        step(diff);
        Gdx.gl.glClearColor(0, 1f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        renderer.setView(camera);
        renderer.render();
        batch.begin();
        for (Unit unit: units) {
            if (unit.clicked) {
                batch.draw(unitTexture, unit.x, unit.y);
            } else {
                batch.draw(unitTexture2, unit.x, unit.y);
            }
        }
        batch.end();
    }
}
