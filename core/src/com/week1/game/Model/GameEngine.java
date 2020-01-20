package com.week1.game.Model;
import com.week1.game.Networking.Messages.IMessage;

import java.util.List;

public class GameEngine {
    private IEngineToRendererAdapter engineToRenderer;
    private GameState gameState;

    public GameEngine(IEngineToRendererAdapter engineToRendererAdapter) {
        gameState = new GameState();
        engineToRenderer = engineToRendererAdapter;

    }

    public void deliverMessage(List<IMessage> message) {

    }

    public GameState getGameState(){
        return gameState;
    }

    public void render(){
        engineToRenderer.startBatch();
        gameState.render((t, x, y) -> {engineToRenderer.draw(t, x, y);} );
        engineToRenderer.endBatch();
    }

}
