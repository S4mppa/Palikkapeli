package com.entity.game.client.world;



import com.entity.game.client.misc.WorldConstants;

import java.util.Objects;

public class ChunkPosition {
    public int x,y,z;

    public ChunkPosition(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;

    }

    @Override
    public boolean equals(Object pos){

        if(!(pos instanceof ChunkPosition)) return false;

        ChunkPosition cpos = (ChunkPosition) pos;

        if(cpos.x == x && cpos.y == y && cpos.z == z) return true;

        return false;
    }

    public void scaleToChunksize(){
        x *= WorldConstants.CHUNKSIZE;
        y *= WorldConstants.CHUNKSIZE;
        z *= WorldConstants.CHUNKSIZE;
    }

    public double dist(double x, double y, double z){
        return Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2) + Math.pow(this.z - z, 2));
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString(){
        return x + "," + y +", " + z;
    }
}
