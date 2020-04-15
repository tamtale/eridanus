package com.week1.game;

import com.badlogic.gdx.graphics.Color;

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
    
    @Override
    public String toString() {
        return "Pair(" + this.key.toString() + ", " + this.value.toString() + ")";
    }

    public static class ColorPair extends Pair<String, Color>{

        public ColorPair(String key, Color value) {
            super(key, value);
        }

        @Override
        public String toString() {
            return key;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Pair)) return false;
        Pair p = (Pair)o;
        return this.key.equals(p.key) && this.value.equals(p.value);
                
    }
    
    @Override
    public int hashCode() {
        return this.key.hashCode() + this.value.hashCode();
    }
}


