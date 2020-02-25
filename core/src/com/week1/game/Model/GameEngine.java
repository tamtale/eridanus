package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.InfoUtil;
import com.week1.game.Model.Entities.Building;
import com.week1.game.Model.Entities.PlayerBase;
import com.week1.game.Model.World.Basic4WorldBuilder;
import com.week1.game.Networking.Messages.Game.CheckSyncMessage;
import com.week1.game.Networking.Messages.Game.GameMessage;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Renderer.RenderConfig;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.List;

import static com.week1.game.GameScreen.THRESHOLD;

public class GameEngine {

    private GameState gameState;
    private int communicationTurn = 0;
    private SpriteBatch batch;
    private IEngineToRendererAdapter engineToRenderer;
    private IEngineToNetworkAdapter engineToNetwork;
    private int enginePlayerId = -1; // Not part of the game state exactly, but used to determine if the game is over for this user
    private InfoUtil util;
    private boolean sentWinLoss = false, sentGameOver = false;
    private boolean isStarted = false;
    BufferedWriter writer;

    public Batch getBatch() {
        return batch;
    }

    public GameEngine(IEngineToRendererAdapter engineToRendererAdapter,IEngineToNetworkAdapter engineToNetworkAdapter, InfoUtil util) {
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
                    engineToRenderer.setDefaultLocation(position);
                });
        Gdx.app.log("wab2- GameEngine", "gameState built");
        batch = new SpriteBatch();
        engineToRenderer = engineToRendererAdapter;
        engineToNetwork =engineToNetworkAdapter;
        this.util = util;

        // Initialize and truncate the log file for the engine and Error log.
        try {
            File logFile = new File("logs/STATE-ERROR-LOG.txt");
            FileChannel outChan = new FileOutputStream(logFile, true).getChannel();
            outChan.truncate(0);

            logFile = new File("logs/LOCAL-SYNC-STATE-LOG.txt");
            writer = new BufferedWriter(new FileWriter(logFile, true));
            outChan = new FileOutputStream(logFile, true).getChannel();
            outChan.truncate(0);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessages(List<? extends GameMessage> messages) {
        communicationTurn += 1;
        Gdx.app.log("ttl4 - receiveMessages", "start of communication turn: " + communicationTurn);

        // Modify things like mana, deal damage, moving units, and checking if the game ends
        synchronousUpdateState();

        // Process the messages that come in, if there are any.
        // prints a message whether or not it has messages to process
        if (messages.isEmpty()) {
            Gdx.app.log("pjb3 - message processing", "Info: queue empty!");
        } else {
            Gdx.app.log("pjb3 - message processing", "Info: queue nonempty!");
        }
        for (GameMessage message : messages) {
            Gdx.app.log("GameEngine: receiveMessages()", "processing message");
            message.process(this, gameState, util);
            Gdx.app.log("GameEngine: receiveMessages()", "done processing message");
        }

        if (communicationTurn % 10 == 0) {
            // Time to sync up!
            engineToNetwork.sendMessage(new CheckSyncMessage(enginePlayerId, MessageType.CHECKSYNC, getGameStateHash(), communicationTurn));

            // Log the state to the file
            try {
                String newContent = "Turn: " + communicationTurn + " hash: " + getGameStateHash() + " String: " + getGameStateString() + "\n";
                writer.append(newContent);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Gdx.app.log("pjb3 - receiveMessages", "end of communication turn: " + communicationTurn);
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

    public void render(RenderConfig renderConfig){
        batch.begin();

        gameState.render(batch, renderConfig, enginePlayerId);
        batch.end();
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

    /**
     * Gets the hash associated with the current state of the game.
     * @return
     */
    public int getGameStateHash() {
        GameState.PackagedGameState wrapped = gameState.packState(communicationTurn);
//        Gdx.app.log("pjb3 - GameEngine", " The entire game state is : \n" + wrapped.getGameString());
        return wrapped.getHash();
    }

    public String getGameStateString() {
        GameState.PackagedGameState wrapped = gameState.packState(communicationTurn);
//        Gdx.app.log("pjb3 - GameEngine", " The entire game state is : \n" + wrapped.getGameString());
        return wrapped.getGameString();
    }

    public int getTurn() {
        return communicationTurn;
    }
}
