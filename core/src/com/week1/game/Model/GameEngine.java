package com.week1.game.Model;
import com.week1.game.Networking.Messages.IMessage;

public class GameEngine {
    private EngineToRenderer adapter;

    public GameEngine(EngineToRenderer engineToRendererAdapter) {
        adapter = engineToRendererAdapter;

    }

    public void deliverMessage(IMessage message) {

    }


}
