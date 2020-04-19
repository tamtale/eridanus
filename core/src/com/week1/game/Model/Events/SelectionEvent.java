package com.week1.game.Model.Events;

import java.util.Collection;

/*
 * Event representing ClickOracle selection of friendly units.
 */
public class SelectionEvent {
    public Collection<Integer> unitIDs; // Treat as immutable.
    public SelectionEvent(Collection<Integer> unitIDs) {
        this.unitIDs = unitIDs;
    }
}
