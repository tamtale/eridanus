package com.week1.game.MenuScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.week1.game.GameControllerSetScreenAdapter;
import com.week1.game.Networking.NetworkObjects.Client;

/**
 * This class helps unify the changing of screens during menu progressions.
 * It is stored within the Client instance so network things can change the screen as
 * commands to change the screen come from the Host
 */
public class ScreenManager {
    private GameControllerSetScreenAdapter gameControllerSetScreenAdapter;
    private boolean isHostScreenManager;
    private GameScreen actualGameScreen;
    private ConnectionScreen connectionScreen;
    private Runnable gameReady;


    public ScreenManager(GameControllerSetScreenAdapter gameControllerSetScreenAdapter, boolean isHost, ConnectionScreen connectionScreen) {
        this.gameControllerSetScreenAdapter = gameControllerSetScreenAdapter;
        this.isHostScreenManager = isHost;
        this.connectionScreen = connectionScreen;
    }

    public void setScreen(Screen screen) {
        Gdx.app.postRunnable(()-> gameControllerSetScreenAdapter.setScreen(screen));
    }

    public Screen getScreen() { return gameControllerSetScreenAdapter.getScreen(); }
    public Screen getGameScreen() {
        return actualGameScreen;
    }
    public ConnectionScreen getConnectionScreen() { return connectionScreen; }

    public void setGameScreen(GameScreen actualGame) {
        this.actualGameScreen = actualGame;
    }

    public void setGameReadySequence(Runnable gs) {
        gameReady = gs;
    }

    public boolean getIsHost() {
        return isHostScreenManager;
    }

    public void createNewLoadoutScreen(Client client) {
        // Set the Screen to the Loadout screen when the render thread is ready
        Gdx.app.postRunnable(() -> gameControllerSetScreenAdapter.setScreen(new LoadoutScreen(client, isHostScreenManager)));
    }

    public void restartGame(Client c) {
        Gdx.app.postRunnable(()-> setScreen(new LoadoutScreen(c, getIsHost())));
    }

    public void setReadyToStart() {
        gameReady.run();
        gameReady = null; // Null it out. Needs to be reset before it is called again on a restart.
    }

    public void goToSplashScreen() {
        Gdx.app.postRunnable(() ->  gameControllerSetScreenAdapter.returnToMainMenu());
    }
}
