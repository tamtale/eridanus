package com.week1.game.Model.Events;

public class DamageEvent {
    public int damagerPlayerID;
    public int damagerID;
    public int victimID;
    public DamageEvent(int damagerPlayerID, int damagerID, int victimID) {
        this.damagerPlayerID = damagerPlayerID;
        this.damagerID = damagerID;
        this.victimID = victimID;
    }
}
