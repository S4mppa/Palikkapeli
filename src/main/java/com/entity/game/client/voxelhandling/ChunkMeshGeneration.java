package com.entity.game.client.voxelhandling;



import com.entity.game.client.misc.BlockData;
import com.entity.game.client.misc.WorldConstants;
import com.entity.game.client.world.Chunk;
import com.entity.game.client.world.ChunkMesh;
import com.entity.game.client.world.MeshFace;

import java.util.*;

public class ChunkMeshGeneration {

    public static int CHUNK_SIZE = 32;

    private static MeshFace FRONT_FACE = new MeshFace(new byte[]{1, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1}, (byte) 0);
    private static MeshFace LEFT_FACE = new MeshFace(new byte[]{0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1}, (byte) 1);
    private static MeshFace BACK_FACE = new MeshFace(new byte[]{0, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0}, (byte) 2);
    private static MeshFace RIGHT_FACE = new MeshFace(new byte[]{1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 0}, (byte) 3);
    private static MeshFace TOP_FACE = new MeshFace(new byte[]{1, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1}, (byte) 4);
    private static MeshFace BOTTOM_FACE = new MeshFace(new byte[]{0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1}, (byte) 5);

    private static MeshFace CROSS_FACE_A = new MeshFace(new byte[] {1, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1}, (byte) 1);
    private static MeshFace CROSS_FACE_B = new MeshFace(new byte[]{1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0}, (byte) 3);

    public static boolean makeFace(Chunk chunk, int x, int y, int z, ChunkManager m, int currentBlock){
        if(BlockData.getBlockByID(currentBlock).transparent() && chunk.safeBlockGet(x,y,z, m) == 0) return true;
        if(!BlockData.getBlockByID(currentBlock).transparent() && BlockData.getBlockByID(chunk.safeBlockGet(x,y,z, m)).transparent()) return true;
        return false;
    }

    private static boolean isVoidAt(Chunk c, ChunkManager chunkManager, int x, int y, int z){
        return chunkManager.getBlockAt(new int[]{x,y,z}) == 0;
    }

    private static boolean typeMatches(int type, ChunkManager chunkManager, int x, int y, int z){
        return chunkManager.getBlockAt(new int[]{x,y,z}) == 0;
    }


    public static ChunkMesh generateChunkMesh(Chunk chunk, ChunkManager m,
                                              HashMap<Integer, BlockData> blockDataMap,
                                              ChunkMeshResourceManager orm,
                                              ChunkMeshResourceManager frm)
    {
        ChunkMesh chunkMesh = new ChunkMesh(chunk.getOrigin(), orm);
        ChunkMesh fluidMesh = new ChunkMesh(chunk.getOrigin(), frm);
        int CHUNKSIZE = WorldConstants.CHUNKSIZE;
        for(int y = 0; y < CHUNKSIZE; y++) {
            for (int z = 0; z < CHUNKSIZE; z++) {
                for (int x = 0; x < CHUNKSIZE; x++) {

                    int blockType = chunk.getBlockAt(x,y,z);
                    if(blockType != 0){
                        ChunkMesh mesh;
                        if(blockDataMap.get(blockType).getBlockID() == 6) mesh = fluidMesh;
                        else mesh = chunkMesh;
                        BlockData blockData = blockDataMap.get(blockType);

                        if(blockData.isCrossFace()){
                            chunk.setHasFlowers(true);
                            mesh.addFace(CROSS_FACE_A, x,y,z, blockData.getSideTexture());
                            mesh.addFace(CROSS_FACE_B, x,y,z, blockData.getSideTexture());
                            continue;
                        }

                        if (blockDataMap.get(chunk.safeBlockGet(x - 1, y, z, m)).transparent() && blockType != 6) {
                            mesh.addFace(LEFT_FACE, x,y,z, blockData.getSideTexture());
                        }
                        if (blockDataMap.get(chunk.safeBlockGet(x + 1, y, z, m)).transparent() && blockType != 6) {
                            mesh.addFace(RIGHT_FACE, x,y,z, blockData.getSideTexture());
                        }

                        if (blockDataMap.get(chunk.safeBlockGet(x, y, z + 1, m)).transparent() && blockType != 6) {
                            mesh.addFace(FRONT_FACE, x,y,z, blockData.getSideTexture());
                        }
                        if (blockDataMap.get(chunk.safeBlockGet(x, y, z - 1, m)).transparent() && blockType != 6 ) {
                            mesh.addFace(BACK_FACE, x,y,z, blockData.getSideTexture());
                        }

                        if (blockDataMap.get(chunk.safeBlockGet(x, y + 1, z, m)).transparent()) {
                            if(blockType == 6){
                                if(chunk.safeBlockGet(x, y + 1, z, m) == 0){
                                    mesh.addFace(TOP_FACE, x,y,z, blockData.getTopTexture());
                                }
                            }
                            else{
                                mesh.addFace(TOP_FACE, x,y,z, blockData.getTopTexture());
                            }
                        }
                        if (blockDataMap.get(chunk.safeBlockGet(x, y - 1, z, m)).transparent() && blockType != 6) {
                            mesh.addFace(BOTTOM_FACE, x,y,z, blockData.getBotTexture());
                        }
                    }

                }
            }
        }
        chunk.setChunkMesh(chunkMesh);
        chunk.setFluidMesh(fluidMesh);
        return chunkMesh;

    }

}
