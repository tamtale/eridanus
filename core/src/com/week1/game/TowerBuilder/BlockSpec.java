package com.week1.game.TowerBuilder;

public class BlockSpec {
    private int blockCode;
    private int x;
    private int y;
    private int z;

    public BlockSpec(int blockCode, int x, int y, int z) {
        this.blockCode = blockCode;
        this.x = x;
        this.y = y;
        this.z = z;

    }

    public int getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(int blockCode) {
        this.blockCode = blockCode;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
