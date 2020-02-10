package com.week1.game.Renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;

import com.week1.game.Model.Direction;
import com.week1.game.InfoUtil;


import java.util.HashMap;
import java.util.Map;

import static com.week1.game.GameScreen.PIXELS_PER_UNIT;

public class Renderer {
    private Batch batch;
    private OrthographicCamera camera;
    private GameButtonsStage gameButtonsStage;
    private Vector3 touchPos = new Vector3();
    private Vector3 defaultPosition = new Vector3(50, 50, 0);
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private IRendererToEngineAdapter engineAdapter;
    private IRendererToNetworkAdapter networkAdapter;
    private IRendererToClickOracleAdapter clickOracleAdapter;
    private RenderConfig renderConfig;
    private BitmapFont font = new BitmapFont();
    private Vector3 panning = new Vector3();
    private Map<Direction, Vector3> directionToVector;
    private static int DEFAULT_WIDTH = 30;

    {
        directionToVector = new HashMap<Direction, Vector3>() {{
            put(Direction.UP, new Vector3(0, 1, 0));
            put(Direction.DOWN, new Vector3(0, -1, 0));
            put(Direction.LEFT, new Vector3(-1, 0, 0));
            put(Direction.RIGHT, new Vector3(1, 0, 0));
            put(Direction.NONE, new Vector3(0, 0, 0));
        }};
    }

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
        camera.setToOrtho(false, DEFAULT_WIDTH, Gdx.graphics.getHeight() * (float) DEFAULT_WIDTH / Gdx.graphics.getWidth());
        camera.update();
    }

    public void zoom(int amount) {
        camera.zoom += amount * .05;
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

    public void resize(int x, int y) {
        float oldX = camera.position.x;
        float oldY = camera.position.y;
        camera.setToOrtho(false, DEFAULT_WIDTH, Gdx.graphics.getHeight() * (float) DEFAULT_WIDTH / Gdx.graphics.getWidth());
        camera.position.x = oldX;
        camera.position.y = oldY;
        camera.update();
        gameButtonsStage.stage.getViewport().update(x, y);
    }

    public void endBatch() {
        batch.end();
    }

    public void endGame(int winOrLoss) {
        winState = winOrLoss;

        gameButtonsStage.endGame(winState);
    }

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
        gameButtonsStage.renderUI((int)engineAdapter.getPlayerMana(networkAdapter.getPlayerId()));
        endBatch();
    }

    public void setPanning(Direction direction) {
        panning.set(directionToVector.get(direction));
    }

    private void updateCamera() {
        // TODO prevent the camera from displaying outside the bounds of the map.
        camera.translate(panning);
        camera.update();
    }

    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        updateCamera();
        mapRenderer.setView(camera);
        mapRenderer.render();
        renderConfig = new RenderConfig(getShowAttackRadius(), getShowSpawnRadius());
        engineAdapter.render(renderConfig);
        clickOracleAdapter.render();
        drawPlayerUI();
        util.drawMessages(batch);
    }

    public InputProcessor getButtonStage() {
        return gameButtonsStage.stage;
    }

    public void setDefaultPosition(Vector3 position) {
        this.defaultPosition.set(position);
    }

    public void setCameraToDefaultPosition() {
        camera.position.set(defaultPosition);
    }

    public boolean getShowAttackRadius() {
        return gameButtonsStage.getShowAttackRadius();
    }

    public boolean getShowSpawnRadius() {
        return gameButtonsStage.getShowSpawnRadius();
    }
}
