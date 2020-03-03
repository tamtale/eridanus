package com.week1.game.Networking.Messages.Game;

/*
 * A game message tagged with its turn number, to be used by the replay queue
 * to deterministically launch events without having to send over the
 * network.
 */
public class TaggedMessage {
    public GameMessage gameMessage;
    public int turn;
    public TaggedMessage(GameMessage gameMessage, int turn) {
        this.gameMessage = gameMessage;
        this.turn = turn;
    }
}
