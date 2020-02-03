package com.week1.game.Model;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.World.Block;

import java.util.Iterator;

public class OutputPath implements GraphPath<Block> {

    private Array<Block> path = new Array<>();
    @Override
    public int getCount() {
        return path.size;
    }

    @Override
    public Block get(int index) {
        return path.get(index);
    }

    @Override
    public void add(Block node) {
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
    public Iterator<Block> iterator() {
        return path.iterator();
    }
}
