package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.InfoUtil;
import com.week1.game.Model.Entities.Building;
import com.week1.game.Model.Entities.Tower;
import com.week1.game.Model.World.CoolWorldBuilder;
import com.week1.game.Networking.Messages.Game.CheckSyncMessage;
import com.week1.game.Networking.Messages.Game.GameMessage;
import com.week1.game.Networking.Messages.Game.TaggedMessage;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Renderer.GameRenderable;
import com.week1.game.Renderer.RenderConfig;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Queue;

import static com.week1.game.MenuScreens.GameScreen.THRESHOLD;

public class GameEngine implements GameRenderable {

    private GameState gameState;
    private int communicationTurn = 0;
    private IEngineAdapter adapter;
    private int enginePlayerId = -1; // Not part of the game state exactly, but used to determine if the game is over for this user
    private InfoUtil util;
    private boolean sentWinLoss = false, sentGameOver = false;
    private Queue<TaggedMessage> replayQueue;
    private boolean isStarted = false;
    BufferedWriter writer;

    public GameEngine(IEngineAdapter adapter, int playerId, Queue<TaggedMessage> replayQueue, InfoUtil util) {
        this.adapter = adapter;
        this.enginePlayerId = playerId;
        this.replayQueue = replayQueue;
        gameState = new GameState(
                CoolWorldBuilder.ONLY,
                () -> {
                    Vector3 position = new Vector3();
                    Tower myBase = gameState.getPlayerBase(this.enginePlayerId);
                    position.set(myBase.getX(), myBase.getY(), 0);
                    adapter.setDefaultLocation(position);
                    adapter.zoom(-20); // 20 is arbitrary, but feels reasonable for initial camera zoom

                    // Give the system the center of the map for camera rotation.
                    int[] dimensions = getGameState().getWorld().getWorldDimensions();
                    adapter.setCenter(new Vector3(dimensions[0] / 2f, dimensions[1] / 2f, dimensions[2] / 2f));
                });
        Gdx.app.log("wab2- GameEngine", "gameState built");
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
        // Modify things like mana, deal damage, moving units, and checking if the game ends
        synchronousUpdateState();
        // Process the messages that come in, if there are any
        for (GameMessage message : messages) {
            message.process(this, gameState, util);
        }
        // Process any messages in the replay queue.
        for (TaggedMessage message = replayQueue.peek(); message != null && message.turn == communicationTurn; message = replayQueue.peek()) {
            replayQueue.poll();
            message.gameMessage.process(this, gameState, util);
        }
        if (communicationTurn % 10 == 0) {
            // Time to sync up!
            adapter.sendMessage(new CheckSyncMessage(enginePlayerId, MessageType.CHECKSYNC, getGameStateHash(), communicationTurn));

            // Log the state to the file
            try {
                String newContent = "Turn: " + communicationTurn + " hash: " + getGameStateHash() + " String: " + getGameStateString() + "\n";
                writer.append(newContent);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void synchronousUpdateState() {
        gameState.updateMana(1);
        gameState.dealDamage(1);
        gameState.moveUnits(THRESHOLD);
        gameState.doTowerSpecialAbilities(communicationTurn);

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

    public Array<Building> getBuildings() {
        return gameState.getBuildings();
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
        return gameState.getTowerDetails(playerId, slot).getCost();
    }
}
