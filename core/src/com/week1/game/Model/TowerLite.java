package com.week1.game.Model;

import com.week1.game.TowerBuilder.BlockSpec;

import java.util.List;

/**
 * This is a tower class that is sent over the network to other players
 */
public class TowerLite {
    String name;
    List<BlockSpec> blocks;

    public TowerLite(String name, List<BlockSpec> blocks) {
        this.name = name;
        this.blocks = blocks;
    }

    public List<BlockSpec> getLayout() {
        return blocks;
    }

    public String getName() {
        return name;
    }
}
