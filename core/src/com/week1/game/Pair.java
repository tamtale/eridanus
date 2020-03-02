package com.week1.game;

/**
 * This is a custom class since importing java's didn't work
 * @param <K>
 * @param <V>
 */
public class Pair<K,V> {
    public K key;
    public V value;
    
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
