package com.week1.game.Model;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.week1.game.Model.Entities.Building;
import com.week1.game.Model.Entities.PlayerBase;
import com.week1.game.Model.World.Basic4WorldBuilder;
import com.week1.game.Model.World.SmallWorldBuilder;
import com.week1.game.Networking.Messages.Game.CreateMinionMessage;
import com.week1.game.Networking.Messages.Game.GameMessage;

import com.badlogic.gdx.math.Vector3;
import com.week1.game.InfoUtil;
import com.week1.game.Networking.Messages.Game.TaggedMessage;
import com.week1.game.Renderer.RenderConfig;

import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.week1.game.GameScreen.THRESHOLD;

public class GameEngine implements RenderableProvider {

    private GameState gameState;
    private ConcurrentLinkedQueue<GameMessage> messageQueue;
    private int communicationTurn = 0;
    private SpriteBatch spriteBatch;
    private IEngineToRendererAdapter engineToRenderer;
    private int enginePlayerId = -1; // Not part of the game state exactly, but used to determine if the game is over for this user
    private InfoUtil util;
    private boolean sentWinLoss = false, sentGameOver = false;
    private Queue<TaggedMessage> replayQueue;

    public Batch getSpriteBatch() {
        return spriteBatch;
    }

    public GameEngine(IEngineToRendererAdapter engineToRendererAdapter, Queue<TaggedMessage> replayQueue, InfoUtil util) {
        messageQueue = new ConcurrentLinkedQueue<>();
        this.replayQueue = replayQueue;
        Gdx.app.log("wab2- GameEngine", "messageQueue built");
        gameState = new GameState(
                SmallWorldBuilder.ONLY,
                () -> {
                    Vector3 position = new Vector3();
                    PlayerBase myBase = null;
                    for (PlayerBase playerBase: gameState.getPlayerBases()) {
                        if (playerBase.getPlayerId() == enginePlayerId) {
                            myBase = playerBase;
                        }
                    }
                    position.set(myBase.getX(), myBase.getY(), 0);
                    engineToRenderer.setDefaultLocation(position);
                });
        Gdx.app.log("wab2- GameEngine", "gameState built");
        spriteBatch = new SpriteBatch();
        engineToRenderer = engineToRendererAdapter;
        this.util = util;
    }

    public void receiveMessages(List<? extends GameMessage> messages) {
        communicationTurn += 1;

        // TODO unit movement should be 'reverted' and then stepped here in the long term so state is consistent.
        synchronousUpdateState();

        // Gdx.app.log("ttl4 - receiveMessages", "communication turn: " + communicationTurn);

        messageQueue.addAll(messages);
    }

    public void synchronousUpdateState() {
        gameState.updateMana(1);
        gameState.dealDamage(1);
        gameState.moveUnits(THRESHOLD);

        // Check the win/loss/restart conditions
        if (!sentWinLoss) {
            if (!gameState.isPlayerAlive(enginePlayerId)) {
                engineToRenderer.endGame(0); // TODO make an enum probably im tired
                sentWinLoss = true;
            } else if (gameState.checkIfWon(enginePlayerId)) {
                engineToRenderer.endGame(1); // TODO same as above
                sentWinLoss = true;
            }
        }
        if (!sentGameOver && gameState.getGameOver()) {
            engineToRenderer.gameOver();
        }
    }

    public void processMessages() {
        for (TaggedMessage replayMsg = replayQueue.peek(); replayMsg != null && replayMsg.turn == communicationTurn; replayMsg = replayQueue.peek()) {
            util.log("processMessages", "got a replayMsg");
            replayMsg.messsage.process(gameState, util);
            replayQueue.poll();
        }
        if (messageQueue.isEmpty()) {
            // Gdx.app.log("ttl4 - message processing", "queue empty!");
            return;
        } else {
            Gdx.app.log("GameEngine: processMessages()", "queue nonempty!");
        }
        for (GameMessage message = messageQueue.poll(); message != null; message = messageQueue.poll()) {
            Gdx.app.log("GameEngine: processMessages()", "processing message");
            message.process(gameState, util);
            Gdx.app.log("GameEngine: processMessages()", "done processing message");
        }
    }

    public void render(RenderConfig renderConfig, ModelBatch modelBatch, Camera cam, Environment env){
        modelBatch.begin(cam);
        modelBatch.render(gameState, env);
        modelBatch.end();
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

    public Array<Building> getBuildings() {
        return gameState.getBuildings();
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        gameState.getRenderables(renderables, pool);
    }
}
