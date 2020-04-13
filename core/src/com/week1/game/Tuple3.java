package com.week1.game;

public class Tuple3<T1, T2, T3> {
    public T1 _1;
    public T2 _2;
    public T3 _3;

    public Tuple3(T1 t1, T2 t2, T3 t3) {
        this._1 = t1;
        this._2 = t2;
        this._3 = t3;
    }

    public void set(T1 t1, T2 t2, T3 t3) {
        this._1 = t1;
        this._2 = t2;
        this._3 = t3;
    }
}
