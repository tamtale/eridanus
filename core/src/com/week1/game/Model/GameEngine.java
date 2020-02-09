package com.week1.game.Model;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.week1.game.Networking.Messages.Game.GameMessage;

import com.badlogic.gdx.math.Vector3;
import com.week1.game.InfoUtil;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameEngine {

    private GameState gameState;
    private ConcurrentLinkedQueue<GameMessage> messageQueue;
    private int communicationTurn = 0;
    private SpriteBatch batch;
    private IEngineToRendererAdapter engineToRenderer;
    private int enginePlayerId = -1; // Not part of the game state exactly, but used to determine if the game is over for this user
    private InfoUtil util;

    public Batch getBatch() {
        return batch;
    }

    public GameEngine(IEngineToRendererAdapter engineToRendererAdapter, InfoUtil util) {
        messageQueue = new ConcurrentLinkedQueue<>();
        Gdx.app.log("wab2- GameEngine", "messageQueue built");
        gameState = new GameState();
        Gdx.app.log("wab2- GameEngine", "gameState built");
        batch = new SpriteBatch();
        engineToRenderer = engineToRendererAdapter;
        this.util = util;
    }

    public void receiveMessages(List<? extends GameMessage> messages) {
        communicationTurn += 1;

        // TODO unit movement should be 'reverted' and then stepped here in the long term so state is consistent.
        synchronousUpdateState();

        Gdx.app.log("ttl4 - receiveMessages", "communication turn: " + communicationTurn);

        messageQueue.addAll(messages);
    }

    public void synchronousUpdateState() {
        gameState.updateMana(1);
        gameState.dealDamage(1);
        if (!gameState.isPlayerAlive(enginePlayerId)) {
            engineToRenderer.endGame(0); // TODO make an enum probably im tired
        } else if (gameState.checkIfWon(enginePlayerId)) {
            engineToRenderer.endGame(1); // TODO same as above
        }
    }

    public void processMessages() {
        if (messageQueue.isEmpty()) {
            Gdx.app.log("ttl4 - message processing", "queue empty!");
            return;
        } else {
            Gdx.app.log("GameEngine: processMessages()", "queue nonempty: " + messageQueue.toString());
        }
        for (GameMessage message = messageQueue.poll(); message != null; message = messageQueue.poll()) {
            Gdx.app.log("GameEngine: processMessages()", "processing message: " + message.toString());
            message.process(gameState, util);
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

    /**
     *
     * @return whether the player that this is associated with is alive or not.
     */
    public boolean isPlayerAlive() {
        if (!started()) {
            return true;
        }
        return gameState.isPlayerAlive(enginePlayerId);
    }

    public void setEnginePlayerId(int playerId) { this.enginePlayerId = playerId; }
}
