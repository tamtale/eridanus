package com.week1.game.Model.Systems;

import java.util.Collection;

public interface Publisher<TEvent> {
    void addSubscriber(Subscriber<TEvent> observer);
    Collection<Subscriber<TEvent>> getSubscribers();
    default void publish(TEvent event) {
        for (Subscriber<TEvent> subscriber: getSubscribers()) {
            subscriber.process(event);
        }
    }
}
