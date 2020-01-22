package com.week1.game.Model;
import com.week1.game.Networking.Messages.AMessage;

import java.util.List;

public class GameEngine {
    private IEngineToRendererAdapter engineToRenderer;
    private GameState gameState;

    public GameEngine(IEngineToRendererAdapter engineToRendererAdapter) {
        gameState = new GameState();
        engineToRenderer = engineToRendererAdapter;

    }

    public void deliverMessage(List<AMessage> message) {

    }

    public void updateState(float delta) {
        gameState.stepUnits(delta);
    }

    public void render(){
        engineToRenderer.batchGame(() -> {gameState.render((t, x, y) -> {engineToRenderer.draw(t, x, y);} );});
    }

}
