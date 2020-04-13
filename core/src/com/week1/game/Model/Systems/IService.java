package com.week1.game.Model.Systems;

/* Interface for systems to query the game state.*/
public interface IService<TQuery, TResult> {
    TResult query(TQuery key);
}
