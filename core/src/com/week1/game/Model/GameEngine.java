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
import com.week1.game.Networking.Messages.Game.GameMessage;

import com.badlogic.gdx.math.Vector3;
import com.week1.game.InfoUtil;
import com.week1.game.Networking.Messages.Game.TaggedMessage;
import com.week1.game.Renderer.RenderConfig;

import java.util.List;
import java.util.Queue;
import com.week1.game.Networking.Messages.Game.CheckSyncMessage;
import com.week1.game.Networking.Messages.MessageType;

import static com.week1.game.GameScreen.THRESHOLD;

public class GameEngine implements RenderableProvider {

    private GameState gameState;
    private int communicationTurn = 0;
    private SpriteBatch spriteBatch;
    private IEngineAdapter adapter;
    private int enginePlayerId = -1; // Not part of the game state exactly, but used to determine if the game is over for this user
    private InfoUtil util;
    private boolean sentWinLoss = false, sentGameOver = false;
    private Queue<TaggedMessage> replayQueue;
    private boolean isStarted = false;

    public GameEngine(IEngineAdapter adapter, Queue<TaggedMessage> replayQueue, InfoUtil util) {
        this.replayQueue = replayQueue;
        this.adapter = adapter;
        Gdx.app.log("wab2- GameEngine", "messageQueue built");
        gameState = new GameState(
                Basic4WorldBuilder.ONLY,
                () -> {
                    Vector3 position = new Vector3();
                    PlayerBase myBase = null;
                    for (PlayerBase playerBase: gameState.getPlayerBases()) {
                        if (playerBase.getPlayerId() == enginePlayerId) {
                            myBase = playerBase;
                        }
                    }
                    position.set(myBase.getX(), myBase.getY(), 0);
                    adapter.setDefaultLocation(position);
                });
        Gdx.app.log("wab2- GameEngine", "gameState built");
        spriteBatch = new SpriteBatch();
        this.util = util;
    }

    public void receiveMessages(List<? extends GameMessage> messages) {
        communicationTurn += 1;
        // Modify things like mana, deal damage, moving units, and checking if the game ends
        synchronousUpdateState();
        for (GameMessage message : messages) {
            message.process(this, gameState, util);
        }
        // Process the replay messages.
        for (TaggedMessage message = replayQueue.peek(); message != null && message.turn == communicationTurn; message = replayQueue.peek()) {
            replayQueue.poll();
            message.gameMessage.process(this, gameState, util);
        }

        if (communicationTurn % 10 == 0) {
            // Time to sync up!
            adapter.sendMessage(new CheckSyncMessage(enginePlayerId, MessageType.CHECKSYNC, getGameStateHash()));
        }
    }

    public void synchronousUpdateState() {
        gameState.updateMana(1);
        gameState.dealDamage(1);
        gameState.moveUnits(THRESHOLD);

        // Check the win/loss/restart conditions
        if (!sentWinLoss) {
            if (!gameState.isPlayerAlive(enginePlayerId)) {
                adapter.endGame(0); // TODO make an enum probably im tired
                sentWinLoss = true;
            } else if (gameState.checkIfWon(enginePlayerId)) {
                adapter.endGame(1); // TODO same as above
                sentWinLoss = true;
            }
        }
        if (!sentGameOver && gameState.getGameOver()) {
            adapter.gameOver();
        }
    }

    public void render(RenderConfig renderConfig, ModelBatch modelBatch, Camera cam, Environment env) {
      // TODO use the renderConfig to interpolate movement
        modelBatch.begin(cam);
        modelBatch.render(gameState, env);
        modelBatch.end();
    }

    public GameState getGameState() {
        return gameState;
    }

    /*
     * whether the host has explicitly sent a message to tell the GameEngine to start
     */
    public boolean started() {
        return isStarted;
    }
    
    public void start() {
        isStarted = true;
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
    /**
     * Gets the hash associated with the current state of the game.
     * @return
     */
    public int getGameStateHash() {
        GameState.PackagedGameState wrapped = gameState.packState();
//        Gdx.app.log("pjb3 - GameEngine", " The entire game state is : \n" + wrapped.getGameString());
        return wrapped.getHash();
    }

    public String getGameStateString() {
        GameState.PackagedGameState wrapped = gameState.packState();
//        Gdx.app.log("pjb3 - GameEngine", " The entire game state is : \n" + wrapped.getGameString());
        return wrapped.getGameString();
    }
}
