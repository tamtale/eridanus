package com.week1.game.Model.Components;

/*
 * Component for entities which are "owned" by players.
 */
public class OwnedComponent {
    public int playerID;
    public OwnedComponent(int playerID) {
        this.playerID = playerID;
    }
}
