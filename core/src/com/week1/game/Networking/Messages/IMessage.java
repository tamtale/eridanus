package com.week1.game.Networking.Messages;

import com.week1.game.Model.GameState;

public interface IMessage {

    boolean process(GameState gameState);
}
