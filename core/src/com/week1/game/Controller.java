package com.week1.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.Model.*;
import com.week1.game.Networking.Client;
import com.week1.game.Networking.NetworkUtils;
import com.week1.game.Renderer.IRendererToEngineAdapter;
import com.week1.game.Renderer.Renderer;

public class Controller {
    private GameEngine engine;
    private ClickOracle clickOracle;
    private Client networkClient;
    private Renderer renderer;
    public Controller(String[] args) {
        networkClient = NetworkUtils.initNetworkObjects(args);
        engine = new GameEngine(new IEngineToRendererAdapter() {
            @Override
            public void startBatch() {
                renderer.startBatch();
            }

            @Override
            public void draw(Texture texture, float x, float y) {
                renderer.draw(texture, x, y);
            }

            @Override
            public void endBatch() {
                renderer.endBatch();
            }
        });
        renderer = new Renderer(new IRendererToEngineAdapter() {
            @Override
            public void render() {
                engine.render();
            }
        });
        clickOracle = new ClickOracle(
                new IClickOracleToRendererAdapter() {
                    @Override
                    public void unproject(Vector3 projected) {

                    }
                },
                new IClickOracleToEngineAdapter() {
                    @Override
                    public Unit selectUnit(Vector3 position) {
                        return null;
                    }

                    @Override
                    public Unit spawn(Vector3 position) {
                        return null;
                    }
                });
    }
}
