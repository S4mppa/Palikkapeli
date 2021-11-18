package com.entity.game.client.misc;


import com.entity.game.client.world.BlockPosition;
import com.entity.game.client.world.ChunkPosition;

public class Coordinates {

    public static ChunkPosition getChunkOriginFromBlock(int x, int y, int z){
        return new ChunkPosition(
                x >> 5,
                y >> 5,
                z >> 5
        );
    }

    public static BlockPosition toLocalBlockPosition(BlockPosition position)
    {
        int CHUNK_SIZE = WorldConstants.CHUNKSIZE;
        return new BlockPosition(position.x & (CHUNK_SIZE-1),
                position.y & (CHUNK_SIZE-1),
                position.z & (CHUNK_SIZE-1));
    }

    public static int[] toLocalBlockPosition(int x, int y, int z)
    {
        int CHUNK_SIZE = WorldConstants.CHUNKSIZE;
        return new int[]{x & (CHUNK_SIZE-1),
            y & (CHUNK_SIZE-1),
            z & (CHUNK_SIZE-1)};
    }


    public static int toLocalBlockIndex(BlockPosition position)
    {
        return position.y * (WorldConstants.CHUNKAREA) + position.z * WorldConstants.CHUNKSIZE + position.x;
    }

    public static int toLocalBlockIndex(int x, int y, int z)
    {
        return y * (WorldConstants.CHUNKAREA) + z * WorldConstants.CHUNKSIZE + x;

    }

    public static BlockPosition toGlobalBlockPosition(BlockPosition blockPosition,
                                     ChunkPosition localChunkPosition)
    {
        return new BlockPosition(localChunkPosition.x + blockPosition.x,
                localChunkPosition.y  + blockPosition.y,
                localChunkPosition.z  + blockPosition.z);
    }

    public static int[] toGlobalBlockPosition(int x, int y, int z,
                                                   ChunkPosition localChunkPosition)
    {
        return new int[]{localChunkPosition.x + x,
                localChunkPosition.y  + y,
                localChunkPosition.z  + z};
    }
}
