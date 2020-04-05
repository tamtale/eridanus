package com.week1.game.Model.Events;

public class DamageEvent {
    public int damagerID;
    public int victimID;
    public DamageEvent(int damagerID, int victimID) {
        this.damagerID = damagerID;
        this.victimID = victimID;
    }
}
