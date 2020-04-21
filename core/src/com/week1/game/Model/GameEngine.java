package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.InfoUtil;
import com.week1.game.Model.Entities.Tower;
import com.week1.game.Model.World.CoolWorldBuilder;
import com.week1.game.Networking.Messages.Game.CheckSyncMessage;
import com.week1.game.Networking.Messages.Game.GameMessage;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Pair;
import com.week1.game.Renderer.GameRenderable;
import com.week1.game.Renderer.RenderConfig;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.List;

public class GameEngine implements GameRenderable {

    private GameState gameState;
    private int communicationTurn = 0;
    private IEngineAdapter adapter;
    private int enginePlayerId = -1; // Not part of the game state exactly, but used to determine if the game is over for this user
    private InfoUtil util;
    private boolean sentWinLoss = false, sentGameOver = false;
    private boolean isStarted = false;
    private static Preferences PREFS;
    BufferedWriter writer;

    public GameEngine(IEngineAdapter adapter, int playerId, InfoUtil util) {
        this.adapter = adapter;
        this.enginePlayerId = playerId;
        this.PREFS = Gdx.app.getPreferences("eridanusSavedContent");
        gameState = new GameState(
                CoolWorldBuilder.ONLY,
//                SmallWorldBuilder.ONLY,
                () -> {
                    Vector3 position = new Vector3();
                    Tower myBase = gameState.getPlayerBase(this.enginePlayerId);
                    position.set(myBase.getX(), myBase.getY(), 0);
                    adapter.setDefaultLocation(position);
                    // Give the system the center of the map for camera rotation.
                    int[] dimensions = getGameState().getWorld().getWorldDimensions();
                    adapter.setCenter(new Vector3(dimensions[0] / 2f, dimensions[1] / 2f, dimensions[2] / 2f));

                    // For development only: change this in the preferences file to 'true' to use target visualization.
                    if (!PREFS.contains("visualTargeting")) {
                        PREFS.putBoolean("visualTargeting", false);
                        PREFS.flush();
                    }
                    if (PREFS.getBoolean("visualTargeting")) {
                        adapter.subscribeSelection(gameState.getSelectionSubscriber());
                    }
                },
                adapter.getPlayerInfo(),
                playerId);
        Gdx.app.log("wab2- GameEngine", "gameState built");
        this.util = util;

        // Initialize and truncate the log file for the engine and Error log.
        try {
            File logFile = Gdx.files.local("logs/STATE-ERROR-LOG.txt").file();
            FileChannel outChan = new FileOutputStream(logFile, true).getChannel();
            outChan.truncate(0);

            logFile = Gdx.files.local("logs/LOCAL-SYNC-STATE-LOG.txt").file();
            writer = new BufferedWriter(new FileWriter(logFile, true));
            outChan = new FileOutputStream(logFile, true).getChannel();
            outChan.truncate(0);
            writer.flush();
        } catch (IOException e) {
            Gdx.app.error("GameEngine", "UNABLE TO CREATE LOG FILES");
        }
    }

    public void receiveMessages(List<? extends GameMessage> messages) {
        communicationTurn += 1;
        if (communicationTurn % 10 == 0) {
            // Time to sync up!
            adapter.sendMessage(new CheckSyncMessage(enginePlayerId, MessageType.CHECKSYNC, getGameStateHash(), communicationTurn));

            // Log the state to the file
            if (writer != null) {
                try {
                    String newContent = "Turn: " + communicationTurn + " hash: " + getGameStateHash() + " String: " + getGameStateString() + "\n";
                    writer.append(newContent);
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Modify things like mana, deal damage, moving units, and checking if the game ends
        synchronousUpdateState();
        // Process the messages that come in, if there are any
        for (int m = 0; m < messages.size(); m++) {
            messages.get(m).process(this, gameState, util);
        }


    }

    public void synchronousUpdateState() {
        gameState.synchronousUpdateState(communicationTurn);
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


    @Override
    public void render(RenderConfig renderConfig) {
        gameState.render(renderConfig);
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

    /**
     * Gets the hash associated with the current state of the game.
     * @return
     */
    public int getGameStateHash() {
        GameState.PackagedGameState wrapped = gameState.packState(communicationTurn);
//        Gdx.app.debug("pjb3 - GameEngine", " The entire game state is : \n" + wrapped.getGameString());
        return wrapped.getHash();
    }

    public String getGameStateString() {
        GameState.PackagedGameState wrapped = gameState.packState(communicationTurn);
//        Gdx.app.debug("pjb3 - GameEngine", " The entire game state is : \n" + wrapped.getGameString());
        return wrapped.getGameString();
    }

    public int getTurn() {
        return communicationTurn;
    }

    public String getTowerName(int playerId, int slot) {
        return gameState.getTowerDetails(playerId, slot).getName();
    }

    public int getTowerCost(int playerId, int slot) {
        return gameState.getTowerDetails(playerId, slot).getPrice();
    }

    public void setFog(boolean enabled) {
        gameState.setFog(enabled);
    }

    public List<Pair<String, Integer>> getCrystalCounts() {
        return gameState.getCrystalCounts();
    }
}
