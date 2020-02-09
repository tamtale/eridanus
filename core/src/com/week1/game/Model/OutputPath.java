package com.week1.game.Model;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;


import java.util.Iterator;

public class OutputPath implements GraphPath<Vector3> {

    private Array<Vector3> path = new Array<>();

    public Array<Vector3> getPath(){
        return path;
    }
    @Override
    public int getCount() {
        return path.size;
    }

    @Override
    public Vector3 get(int index) {

        return path.get(index);
    }

    @Override
    public void add(Vector3 node) {
        path.add(node);
    }

    @Override
    public void clear() {
        path.clear();
    }

    @Override
    public void reverse() {
        path.reverse();
    }

    @Override
    public Iterator<Vector3> iterator() {
        return path.iterator();
    }
}
