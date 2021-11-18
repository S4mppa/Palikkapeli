package com.entity.game.client.events;


import com.entity.game.client.misc.BlockData;
import com.entity.game.client.world.BlockPosition;
import com.entity.game.client.world.Chunk;
import com.entity.game.client.world.World;

public class BlockBreakEvent extends Event {
    private BlockData blockData;
    private BlockPosition blockPosition;
    private Chunk chunk;
    private World world;
    public BlockBreakEvent(World world, BlockData blockData, BlockPosition blockPosition, Chunk chunk){
        this.blockData = blockData;
        this.blockPosition = blockPosition;
        this.chunk = chunk;
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public BlockPosition getBlockPosition() {
        return blockPosition;
    }

    public BlockData getBlockData() {
        return blockData;
    }
}
