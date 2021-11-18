package com.entity.game.client.world;

public class BlockPosition {
    public int x,y,z;
    public BlockPosition(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    @Override
    public String toString() {
        return String.format("X: %d Y: %d Z: %d", x,y,z);
    }
}
