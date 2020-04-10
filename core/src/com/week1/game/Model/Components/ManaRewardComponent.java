package com.week1.game.Model.Components;

public class ManaRewardComponent extends AComponent {
    public int deathReward;    // A single reward given to the killer
    public float damageReward; // Multiplied by the damage dealt to determine a reward for each hit
    public ManaRewardComponent(int deathReward, float damageReward) {
        this.deathReward = deathReward;
        this.damageReward = damageReward;
    }
}
