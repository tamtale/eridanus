package com.week1.game.Model;

import com.week1.game.Model.World.Block;

public interface Unit2StateAdapter {

    public Block getBlock(int i, int j, int k);
    public int getHeight(int i, int j);
}
