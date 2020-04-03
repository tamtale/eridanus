package com.week1.game.Model.Components;

/*
 * Component for an entity currently targeting another.
 */
public class TargetingComponent extends AComponent {
    public int targetID; // -1 if no current target.
    public float range; // Targeting range.
    public boolean switchTargets; // Whether or not to switch targets when out of range.
    public TargetingComponent(int targetID, float range, boolean switchTargets) {
        this.targetID = targetID;
        this.range = range;
        this.switchTargets = switchTargets;
    }
}
