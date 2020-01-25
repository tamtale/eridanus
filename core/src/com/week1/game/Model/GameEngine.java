package com.week1.game.Model;
import com.badlogic.gdx.Gdx;
import com.week1.game.Networking.Messages.Game.GameMessage;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.Networking.Messages.AMessage;
import com.week1.game.Networking.Messages.Game.GameMessage;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameEngine {

    private IEngineToRendererAdapter engineToRenderer;
    private GameState gameState;
    private ConcurrentLinkedQueue<GameMessage> messageQueue;
    private int communicationTurn = 0;

    public GameEngine(IEngineToRendererAdapter engineToRendererAdapter) {
        messageQueue = new ConcurrentLinkedQueue<>();
        gameState = new GameState();
        engineToRenderer = engineToRendererAdapter;
    }

    public void receiveMessages(List<? extends GameMessage> messages) {
        communicationTurn += 1;
        Gdx.app.log("ttl4 - receiveMessages", "communication turn: " + communicationTurn);
        messageQueue.addAll(messages);
    }

    public void processMessages() {
        if (messageQueue.isEmpty()) {
            Gdx.app.log("ttl4 - message processing", "queue empty!");
            return;
        } else {
            Gdx.app.log("GameEngine: processMessages()", "queue nonempty!");
        }
        for (GameMessage message = messageQueue.poll(); message != null; message = messageQueue.poll()) {
            Gdx.app.log("GameEngine: processMessages()", "processing message");
            message.process(gameState);
        }
    }

    public void updateState(float delta) {
        gameState.stepUnits(delta);
    }

    public void render(){
        engineToRenderer.batchGame(() -> {gameState.render((t, x, y) -> {engineToRenderer.draw(t, x, y);} );});
    }

    public GameState getGameState() {
        return gameState;
    }

    /*
     * Whether or not the first communication message has been received from the host.
     */
    public boolean started() {
        return communicationTurn > 0;
    }

    public void updateGoal(Unit unit, Vector3 goal) {
        gameState.updateGoal(unit, goal);
    }
}
