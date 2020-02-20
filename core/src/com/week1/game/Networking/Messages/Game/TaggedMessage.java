package com.week1.game.Networking.Messages.Game;

public class TaggedMessage {
    public GameMessage messsage;
    public int turn;
    public TaggedMessage(GameMessage message, int turn) {
        this.messsage = message;
        this.turn = turn;
    }
}
