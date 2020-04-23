package com.week1.game.Model.Components;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/*
 * Component for an entity currently targeting another.
 */
public class TargetingComponent extends AComponent {
    public int permission;
    public int targetID; // -1 if no current target.
    public int intentID = -1; // ID that the entity intends to target when possible. -1 if none.
    public float range; // Targeting range.
    public boolean switchTargets; // Whether or not to switch targets when out of range.
    public TargetingStrategy strategy;
    public TargetingComponent(int targetID, float range, boolean switchTargets, TargetingStrategy strategy, int permission) {
        this.targetID = targetID;
        this.range = range;
        this.switchTargets = switchTargets;
        this.strategy = strategy;
        this.permission = permission;
    }

    public enum TargetingStrategy {
        ENEMY, TEAM, ALL
    }

    // Permissions
    public static int P_MINIONS = 1;
    public static int P_MINIONS_TOWERS = 2;
    public static int P_MINIONS_TOWERS_CRYSTALS = 3;
}
