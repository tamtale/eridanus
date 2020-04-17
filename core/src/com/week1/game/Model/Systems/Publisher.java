package com.week1.game.Model.Systems;

interface Publisher<TEvent> {
    void addSubscriber(Subscriber<TEvent> observer);
}
