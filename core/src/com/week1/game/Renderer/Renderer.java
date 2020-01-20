package com.week1.game.Renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.Model.GameState;
import com.week1.game.Model.Unit;

public class Renderer {

    public static int SCALE = 8; // 8 pixels per unit.
    private Batch batch;
    private OrthographicCamera camera;
    private Pixmap unitPixmap;
    private Pixmap unitPixmap2;
    private Texture unitTexture;
    private Texture unitTexture2;
    private Vector3 touchPos = new Vector3();
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private IRenderer2EngineAdapter adapter;

    public void create(GameState state) {
        map = new TmxMapLoader().load("testmap.tmx");
        camera = new OrthographicCamera();
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f / SCALE);
        camera.setToOrtho(false, 256, 256);
        camera.update();
        unitPixmap = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888);

        unitPixmap.setColor(Color.BLUE);
        unitPixmap.fill();

        unitPixmap2 = new Pixmap(SCALE, SCALE, Pixmap.Format.RGB888);
        unitPixmap2.setColor(Color.RED);
        unitPixmap2.fill();

        unitTexture = new Texture(unitPixmap);
        unitTexture2 = new Texture(unitPixmap2);
    }

    public void render(GameState state) {
        float diff = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0, 1f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
        adapter.drawUnits(batch);
    }
}
