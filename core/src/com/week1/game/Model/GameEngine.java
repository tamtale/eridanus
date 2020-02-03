package com.week1.game.Model;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.week1.game.Networking.Messages.Game.GameMessage;
import com.badlogic.gdx.math.Vector3;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameEngine {

    private GameState gameState;
    private ConcurrentLinkedQueue<GameMessage> messageQueue;
    private int communicationTurn = 0;
    private SpriteBatch batch;

    public Batch getBatch() {
        return batch;
    }

    public GameEngine() {
        messageQueue = new ConcurrentLinkedQueue<>();
        gameState = new GameState();
        batch = new SpriteBatch();
    }

    public void receiveMessages(List<? extends GameMessage> messages) {
        communicationTurn += 1;
         // This needs to be synchronized with the communication turn.
        // TODO unit movement should be 'reverted' and then stepped here in the long term so state is consistent.
        synchronousUpdateState();

        Gdx.app.log("ttl4 - receiveMessages", "communication turn: " + communicationTurn);
        messageQueue.addAll(messages);
    }

    public void synchronousUpdateState() {
        gameState.updateMana(1);
        gameState.dealDamage(1);
        if (!gameState.isPlayerAlive()) {
            engineToRenderer.endGame(0); // TODO make an enum probably im tired
        } else if (gameState.checkIfWon()) {
            engineToRenderer.endGame(1); // TODO same as above
        }
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
