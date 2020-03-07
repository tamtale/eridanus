package com.week1.game.Model;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.SmoothableGraphPath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;


import java.util.Iterator;

public class OutputPath implements SmoothableGraphPath<Vector2, Vector2> {

    private Array<Vector2> path = new Array<>();

    private Vector2 tmpPosition = new Vector2();

    public Array<Vector2> getPath(){
        return path;
    }
    @Override
    public int getCount() {
        return path.size;
    }

    @Override
    public Vector2 get(int index) {

        return path.get(index);
    }

    @Override
    public void add(Vector2 node) {
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
    public Iterator<Vector2> iterator() {
        return path.iterator();
    }

    public void removeIndex(int index){
        path.removeIndex(index);
    }

    @Override
    public Vector2 getNodePosition(int index) {
        Vector2 node = path.get(index);
        return tmpPosition.set(node.x, node.y);
    }

    @Override
    public void swapNodes(int index1, int index2) {
        path.set(index1, path.get(index2));
    }

    @Override
    public void truncatePath(int newLength) {
        path.truncate(newLength);
    }
}
