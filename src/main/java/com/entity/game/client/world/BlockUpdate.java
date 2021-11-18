package com.entity.game.client.world;

public class BlockUpdate {
    public int x,y,z,w;

    public BlockUpdate(int x, int y, int z, int w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public BlockPosition getBlockPos(){
        return new BlockPosition(x,y,z);
    }
}
