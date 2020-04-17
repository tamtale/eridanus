package com.week1.game.Model;

public class EntityManager {
    private int cur_id = 0;
    public int newID() {
        return cur_id++;
    }
}
