package com.week1.game.Renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;

import com.week1.game.Model.Direction;
import com.week1.game.InfoUtil;


import java.util.HashMap;
import java.util.Map;

public class Renderer {
    private Batch batch = new SpriteBatch();
    public ModelBatch modelBatch;
    public Model model;
    public ModelInstance instance;
    private PerspectiveCamera cam;
    private GameButtonsStage gameButtonsStage;
    private Environment env;
    private Vector3 touchPos = new Vector3();
    private Vector3 defaultPosition = new Vector3(50, 50, 0);
    private IRendererToEngineAdapter engineAdapter;
    private IRendererToNetworkAdapter networkAdapter;
    private IRendererToClickOracleAdapter clickOracleAdapter;
    private IRendererToGameScreenAdapter gameScreenAdapter;
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

    public Renderer(IRendererToEngineAdapter engineAdapter,
                    IRendererToNetworkAdapter networkAdapter,
                    IRendererToClickOracleAdapter clickOracleAdapter,
                    IRendererToGameScreenAdapter gameScreenAdapter,
                    InfoUtil util) {
        this.engineAdapter = engineAdapter;
        this.networkAdapter = networkAdapter;
        this.clickOracleAdapter = clickOracleAdapter;
        this.util = util;
        this.gameScreenAdapter = gameScreenAdapter;
    }

    public ModelBatch getModelBatch() {
        return modelBatch;
    }

    public Environment getEnv() { return env; }

    public PerspectiveCamera getCam() {
        return cam;
    }

    public void create() {
        modelBatch = new ModelBatch();
        env = new Environment();
        env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(-8, 20, 30);
        // cam.lookAt(10,15,0);
        cam.rotate(Vector3.X, 30f);
        cam.position.set(-8, 20, 30);
        cam.near = 1f;
        cam.far = 500f;
        cam.update();
        gameButtonsStage = new GameButtonsStage(clickOracleAdapter, gameScreenAdapter);
        cam.update();
    }

    public void render3D(RenderableProvider provider) {
        modelBatch.begin(cam);
        modelBatch.render(provider, env);
        modelBatch.end();
    }

    public Camera getCamera() {
        return cam;
    }

    public void startBatch() {
        batch.begin();
    }

    public void draw(Texture t, float x, float y) {
        batch.draw(t, x, y);
    }

    public void resize(int x, int y) {
        // TODO this
        cam.viewportWidth = x;
        cam.viewportHeight = y;
        cam.update();
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
        cam.translate(panning);
        cam.update();
    }

    public void render(float deltaTime) {
        // Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        updateCamera();
        renderConfig = new RenderConfig(getShowAttackRadius(), getShowSpawnRadius(), deltaTime);
        engineAdapter.render(renderConfig);
        clickOracleAdapter.render();
        drawPlayerUI();
        util.drawMessages(batch);
        Gdx.app.log("camera pos: ", cam.position.toString());
    }

    public InputProcessor getButtonStage() {
        return gameButtonsStage.stage;
    }

    public void setDefaultPosition(Vector3 position) {
        this.defaultPosition.set(position);
    }

    public void setCameraToDefaultPosition() {
        cam.position.set(defaultPosition);
    }

    public boolean getShowAttackRadius() {
        return gameButtonsStage.getShowAttackRadius();
    }

    public boolean getShowSpawnRadius() {
        return gameButtonsStage.getShowSpawnRadius();
    }

    public void showGameOver() {
        gameButtonsStage.setGameOver();
    }
}
