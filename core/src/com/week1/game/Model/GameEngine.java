package com.week1.game.Model;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.week1.game.Networking.Messages.Game.GameMessage;
import com.badlogic.gdx.math.Vector3;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameEngine {

    private IEngineToRendererAdapter engineToRenderer;
    private GameState gameState;
    private ConcurrentLinkedQueue<GameMessage> messageQueue;
    private int communicationTurn = 0;
    private SpriteBatch batch;

    public Batch getBatch() {
        return batch;
    }

    public GameEngine(IEngineToRendererAdapter engineToRendererAdapter) {
        messageQueue = new ConcurrentLinkedQueue<>();
        engineToRenderer = engineToRendererAdapter;
        gameState = new GameState();
        batch = new SpriteBatch();
    }

    public void receiveMessages(List<? extends GameMessage> messages) {
        communicationTurn += 1;
        gameState.updateMana(1); // This needs to be synchronized with the communication turn TODO is this the best way to do that?
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
            Gdx.app.log("GameEngine: processMessages()", "done processing message");
        }
    }

    public void updateState(float delta) {
        gameState.stepUnits(delta);
        gameState.dealDamage(delta);
//        gameState.updateMana(delta); // TODO decide where we want mana updated.
    }

    public void render(){
        batch.begin();
        gameState.render(batch);
        batch.end();
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
