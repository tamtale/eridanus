package com.week1.game.MenuScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.week1.game.GameControllerSetScreenAdapter;
import com.week1.game.GameScreen;
import com.week1.game.Networking.NetworkObjects.Tcp.TcpClient;

/**
 * This class is passed around and helps unify the changing of screens during menu progressions.
 */
public class ScreenManager {
    private GameControllerSetScreenAdapter gameControllerSetScreenAdapter;
    private boolean isHostScreenManager;
    private GameScreen actualGameScreen;

    public ScreenManager(GameControllerSetScreenAdapter gameControllerSetScreenAdapter, boolean isHost) {
        this.gameControllerSetScreenAdapter = gameControllerSetScreenAdapter;
        this.isHostScreenManager = isHost;
    }

    public void setScreen(Screen screen) {
        Gdx.app.postRunnable(()-> gameControllerSetScreenAdapter.setScreen(screen));
    }

    public Screen getGameScreen() {
        return actualGameScreen;
    }

    public void setGameScreen(GameScreen actualGame) {
        this.actualGameScreen = actualGame;
    }

    public boolean getIsHost() {
        return isHostScreenManager;
    }

    public void createNewLoadoutScreen(TcpClient tcpClient) {
        // Set the Screen to the Loadout screen when the render thread is ready
        Gdx.app.postRunnable(() -> gameControllerSetScreenAdapter.setScreen(new LoadoutScreen(gameControllerSetScreenAdapter, tcpClient, isHostScreenManager)));
    }
}
