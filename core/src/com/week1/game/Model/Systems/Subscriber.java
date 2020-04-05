package com.week1.game.Model.Systems;

public interface Subscriber<TEvent> {
    void process(TEvent event);
}
