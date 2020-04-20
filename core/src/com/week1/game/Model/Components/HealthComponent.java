package com.week1.game.Model.Components;

public class HealthComponent extends AComponent {
    public float maxHealth;
    public float curHealth;
    public float growthRate;

    public HealthComponent(float maxHealth, float curHealth) {
        this.maxHealth = maxHealth;
        this.curHealth = curHealth;
    }

    public HealthComponent(float maxHealth, float curHealth, float growthRate){
        this(maxHealth, curHealth);
        this.growthRate = growthRate;
    }
}
