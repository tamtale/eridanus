package com.week1.game.Renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;

import com.week1.game.InfoUtil;


import static com.week1.game.GameScreen.PIXELS_PER_UNIT;

public class Renderer {
    private Batch batch;
    private OrthographicCamera camera;
    private GameButtonsStage gameButtonsStage;
    private Vector3 touchPos = new Vector3();
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private IRendererToEngineAdapter engineAdapter;
    private IRendererToNetworkAdapter networkAdapter;
    private IRendererToClickOracleAdapter clickOracleAdapter;
    private BitmapFont font = new BitmapFont();

    private int winState = -1;
    private InfoUtil util;

    public Renderer(IRendererToEngineAdapter engineAdapter, IRendererToNetworkAdapter networkAdapter, IRendererToClickOracleAdapter clickOracleAdapter, InfoUtil util) {
        this.engineAdapter = engineAdapter;
        this.networkAdapter = networkAdapter;
        this.clickOracleAdapter = clickOracleAdapter;
        this.util = util;
    }

    public void create() {
        map = engineAdapter.getMap();
        camera = new OrthographicCamera();
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f / PIXELS_PER_UNIT);
        batch = mapRenderer.getBatch();
        gameButtonsStage = new GameButtonsStage(clickOracleAdapter);
        camera.setToOrtho(false, 100, 100);
        camera.update();
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

    public void endGame(int winOrLoss) { winState = winOrLoss; }

    public void renderInfo() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        startBatch();
        font.setColor(Color.WHITE);
        font.draw(batch, "Host IP:" + networkAdapter.getHostAddr(), 50, 50);
        endBatch();
    }

    public void drawPlayerUI() {
        startBatch();
        font.getData().setScale(1f);
        font.setColor(Color.BLUE);
        font.draw(batch, String.format("Mana: %d", (int)engineAdapter.getPlayerMana(networkAdapter.getPlayerId())), 20, 14);
        if (winState == 1) {
            font.draw(batch, "YOU WIN!!", 20, 50);
        } else if (winState == 0) {
            font.draw(batch, "YOU LOST", 20, 50);
        }
        endBatch();
    }

    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
        engineAdapter.render();
        clickOracleAdapter.render();
        drawPlayerUI();
        util.drawMessages(batch, font);

        gameButtonsStage.render();
    }

    public InputProcessor getButtonStage() {
        return gameButtonsStage.stage;
    }
}
