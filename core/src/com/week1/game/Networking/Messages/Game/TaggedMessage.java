package com.week1.game.Networking.Messages.Game;

public class TaggedMessage {
    public GameMessage gameMessage;
    public int turn;
    public TaggedMessage(GameMessage gameMessage, int turn) {
        this.gameMessage = gameMessage;
        this.turn = turn;
    }
}
