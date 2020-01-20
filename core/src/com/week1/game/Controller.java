package com.week1.game;

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
        });
        renderer = new Renderer(new IRendererToEngineAdapter() {
            @Override
            public void drawUnits(Batch batch) {
                // TODO
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
