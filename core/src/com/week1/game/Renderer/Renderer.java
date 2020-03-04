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
    public Model model;
    private PerspectiveCamera cam;
    private GameButtonsStage gameButtonsStage;
    private Environment env;
    private Vector3 defaultPosition = new Vector3(50, 50, 0);
    private IRendererAdapter adapter;
    private RenderConfig renderConfig;
    private BitmapFont font = new BitmapFont();
    private Vector3 panning = new Vector3();
    private Map<Direction, Vector3> directionToVector;

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

    public Renderer(IRendererAdapter clickOracleAdapter,
                    InfoUtil util) {
        this.adapter = clickOracleAdapter;
        this.util = util;
    }

    public PerspectiveCamera getCam() {
        return cam;
    }
    public RenderConfig getRenderConfig() { return renderConfig; }

    public void create() {
        env = new Environment();
        env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderConfig = new RenderConfig(false, false, 0, cam, env);
        cam.position.set(-8, 20, 30);
        // cam.lookAt(10,15,0);
        cam.rotate(Vector3.X, 45f);
        cam.position.set(-8, 20, 30);
        cam.near = 1f;
        cam.far = 500f;
        cam.update();
        gameButtonsStage = new GameButtonsStage(adapter);
        cam.update();
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
        cam.viewportWidth = x;
        cam.viewportHeight = y;
        cam.update();
        renderConfig.update();
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
        font.draw(batch, "Host IP:" + adapter.getHostAddr(), 50, 50);
        endBatch();
    }

    public void drawPlayerUI() {
        startBatch();
        gameButtonsStage.renderUI((int) adapter.getPlayerMana(adapter.getPlayerId()));
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
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        updateCamera();
        renderConfig.set(getShowAttackRadius(), getShowSpawnRadius(), deltaTime);
        adapter.renderSystem(renderConfig);
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
