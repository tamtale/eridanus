package com.week1.game.Model.Systems;

/*
 * Top-level interface for a system,
 * which is updated from the game loop by some delta.
 */
public interface ISystem {
    void update(float delta);
}
