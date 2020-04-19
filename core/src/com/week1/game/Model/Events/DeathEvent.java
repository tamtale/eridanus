package com.week1.game.Model.Events;

public class DeathEvent extends DamageEvent {
    public DeathEvent(int damagerPlayerID, int damagerID, int victimID) {
        super(damagerPlayerID, damagerID, victimID);
    }
    public static DeathEvent fromDamage(DamageEvent event) {
        return new DeathEvent(event.damagerPlayerID, event.damagerID, event.victimID);
    }
}
