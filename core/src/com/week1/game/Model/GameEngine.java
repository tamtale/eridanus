package com.week1.game.Model;
import com.week1.game.Networking.Messages.IMessage;

import java.util.List;

public class GameEngine {
    private IEngineToRendererAdapter adapter;

    public GameEngine(IEngineToRendererAdapter engineToRendererAdapter) {
        adapter = engineToRendererAdapter;

    }

    public void deliverMessage(List<IMessage> message) {

    }


}
