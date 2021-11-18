package com.entity.game.client.voxelhandling;



import com.entity.game.client.misc.WorldConstants;

import java.util.Arrays;

public class ChunkMeshResourceManager {
    public int[] indices = new int[(int) (WorldConstants.CHUNKVOLUME * 3f)];
    public int[] vertexData = new int[(int) (WorldConstants.CHUNKVOLUME * 2.5f)];

    public void clear(){
        Arrays.fill(indices, 0);
        Arrays.fill(vertexData, 0);
    }
}
