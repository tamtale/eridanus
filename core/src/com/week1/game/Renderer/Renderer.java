package com.week1.game.Renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.InfoUtil;
import com.week1.game.Model.Direction;
import com.week1.game.Model.RotationDirection;

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
    private Vector3 panning = new Vector3();
    private Vector3 mapCenter = new Vector3();
    private Texture spaceBackground;
    static Pixmap spacePix = new Pixmap(Gdx.files.internal("starfield.png"));
    private float deltaRotation = 0f;
    private float totalRotation = 0f;
    private static float PAN_SCALE = 1.5f;
    private Map<Direction, Vector3> directionToVector;
    {
        directionToVector = new HashMap<Direction, Vector3>() {{
            put(Direction.UP, new Vector3(0, 1, 0).scl(PAN_SCALE));
            put(Direction.DOWN, new Vector3(0, -1, 0).scl(PAN_SCALE));
            put(Direction.LEFT, new Vector3(-1, 0, 0).scl(PAN_SCALE));
            put(Direction.RIGHT, new Vector3(1, 0, 0).scl(PAN_SCALE));
            put(Direction.NONE, new Vector3(0, 0, 0).scl(PAN_SCALE));
        }};
    }

    private int winState = -1;
    private InfoUtil util;

    public Renderer(IRendererAdapter rendererAdapter,
                    InfoUtil util) {
        this.adapter = rendererAdapter;
        this.util = util;
        
        env = new Environment();
        env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderConfig = new RenderConfig(false, false, 0, cam, env);
    }

    public PerspectiveCamera getCam() {
        return cam;
    }
    public RenderConfig getRenderConfig() { return renderConfig; }

    public void create() {
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

    public void zoom(float amount) {
        // Might need a camera update, but seems to happen often enough on the render loop that 
        // it doesn't cause problems to omit here
        Camera camera = renderConfig.getCam();
        camera.translate(new Vector3(camera.direction).scl(amount));
    }
    
    public void resize(int x, int y) {
        // Remake the background with the correct scale.
        Pixmap spacePixScaled = new Pixmap(x, y, spacePix.getFormat());
        spacePixScaled.drawPixmap(spacePix,
                0, 0, spacePix.getWidth(), spacePix.getHeight(),
                0, 0, spacePixScaled.getWidth(), spacePixScaled.getHeight()
        );
        spaceBackground = new Texture(spacePixScaled);
        spacePixScaled.dispose();


        cam.viewportWidth = x;
        cam.viewportHeight = y;
        cam.update();
        renderConfig.update();
        gameButtonsStage.stage.getViewport().apply();
        gameButtonsStage.stage.getViewport().update(x, y);
    }

    public void endBatch() {
        batch.end();
    }

    public void endGame(int winOrLoss) {
        winState = winOrLoss;

        gameButtonsStage.endGame(winState);
    }

    public void drawPlayerUI() {
        startBatch();
        gameButtonsStage.renderUI((int) adapter.getPlayerMana(adapter.getPlayerId()));
        endBatch();
    }

    public void setPanning(Direction direction) {
        panning.set(directionToVector.get(direction));
        // Make sure panning is relative to the current rotation.
        panning.rotate(Vector3.Z, totalRotation);
    }

    public void setDeltaRotation(RotationDirection direction) {
        switch (direction) {
            case CLOCKWISE:
                deltaRotation = 1f;
                break;
            case COUNTERCLOCKWISE:
                deltaRotation = -1f;
                break;
            case NONE:
                deltaRotation = 0f;
                break;
        }
    }

    private void updateCamera() {
        // TODO prevent the camera from displaying outside the bounds of the map.
        cam.rotateAround(mapCenter, Vector3.Z, deltaRotation);
        totalRotation += deltaRotation;
        // Keep the rotation within [0, 360f]
        while (totalRotation > 360f) {
            totalRotation -= 360f;
        }
        while (totalRotation < 0f) {
            totalRotation += 360f;
        }
        cam.translate(panning);
        cam.update();
    }

    public void render(float deltaTime) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch.begin();
        batch.draw(spaceBackground, 0,0);
        batch.end();

        updateCamera();
        renderConfig.set(getShowAttackRadius(), getShowSpawnRadius(), deltaTime);
        adapter.renderSystem(renderConfig);
        drawPlayerUI();
        util.drawMessages(batch);
    }

    public GameButtonsStage getGameButtonsStage() {
        return gameButtonsStage;
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

    public void setCenter(Vector3 newCenter) {
        mapCenter.set(newCenter);
    }
}
