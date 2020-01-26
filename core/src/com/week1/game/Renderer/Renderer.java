package com.week1.game.Renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.Model.GameState;
import jdk.jfr.internal.test.WhiteBox;

import static com.week1.game.GameController.SCALE;

public class Renderer {
    private Batch batch;
    private OrthographicCamera camera;
    private Pixmap unitPixmap;
    private Pixmap unitPixmap2;
    private Texture unitTexture;
    private Texture unitTexture2;
    private Vector3 touchPos = new Vector3();
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private IRendererToEngineAdapter engineAdapter;
    private IRendererToNetworkAdapter networkAdapter;
    private BitmapFont font = new BitmapFont();

    public Renderer(IRendererToEngineAdapter engineAdapter, IRendererToNetworkAdapter networkAdapter) {
        this.engineAdapter = engineAdapter;
        this.networkAdapter = networkAdapter;
    }


    public void create() {
        map = new TmxMapLoader().load("gridmap.tmx");
        camera = new OrthographicCamera();
        mapRenderer = new OrthogonalTiledMapRenderer(map, 2f / SCALE);
        batch = mapRenderer.getBatch();
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

    public Camera getCamera() {
        return camera;
    }

    public void startBatch() {
        batch.begin();
    }

    public void draw(Texture t, float x, float y) {
        batch.draw(t, x, y);
    }

    public void endBatch() {
        batch.end();
    }

    public void renderInfo() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        startBatch();
        font.setColor(Color.WHITE);
        font.draw(batch, "Host IP:" + networkAdapter.getHostAddr(), 50, 50);
        endBatch();
    }

    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
        engineAdapter.render();
    }
}
