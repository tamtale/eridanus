package com.week1.game.Model;

public class TowerDetails {
    public TowerFootprint footprint;
    public double health;
    public double price;
    public double range;
    public double damage;
    
    public TowerDetails(TowerFootprint footprint, double health, double price, double range, double damage) {
        this.footprint = footprint;
        this.health = health;
        this.price = price;
        this.range = range;
        this.damage = damage;
    }
}
