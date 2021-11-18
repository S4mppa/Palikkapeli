package com.entity.game.client.world;


import com.entity.game.client.misc.WorldConstants;
import com.entity.game.client.renderEngine.Loader;
import com.entity.game.client.voxelhandling.ChunkMeshResourceManager;

public class ChunkMesh {

    private int CHUNK_SIZE = WorldConstants.CHUNKSIZE;
    private int CHUNK_VOLUME = CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE;
    private int indicesCount = 0;
    private ChunkPosition chunkOrigin;
    private ChunkMeshResourceManager rm;
    private Loader loader;
    private int verI = 0;
    private int indI = 0;
    private int texI = 0;
    private int bloI = 0;

    public ChunkMesh(ChunkPosition chunkOrigin, ChunkMeshResourceManager rm){

        this.chunkOrigin = chunkOrigin;
        this.loader = new Loader();
        this.rm = rm;
    }

    public ChunkPosition getChunkOrigin(){
        return chunkOrigin;
    }

    public void addFace(MeshFace face, int bx, int by, int bz, int texture){
        int index = 0;
        for (int i = 0; i < 4; i++) {
            int x = face.vertices[index++] + bx;
            int y = face.vertices[index++] + by;
            int z = face.vertices[index++] + bz;

            int vertex = x | y << 6 | z << 12 | face.lightLevel << 18 | i << 21 | texture << 23;
            rm.vertexData[verI++] = vertex;
        }
        rm.indices[indI++] = indicesCount;
        rm.indices[indI++] = indicesCount+1;
        rm.indices[indI++] = indicesCount+2;
        rm.indices[indI++] = indicesCount+2;
        rm.indices[indI++] = indicesCount+3;
        rm.indices[indI++] = indicesCount;
        indicesCount += 4;
    }

    public void addQuad(MeshFace face, int texture){
        int index = 0;
        for (int i = 0; i < 4; i++) {
            int x = face.vertices[index++];
            int y = face.vertices[index++];
            int z = face.vertices[index++];

            int vertex = x | y << 6 | z << 12 | face.lightLevel << 18 | i << 21 | texture << 23;
            rm.vertexData[verI++] = vertex;
        }
        rm.indices[indI++] = indicesCount;
        rm.indices[indI++] = indicesCount+1;
        rm.indices[indI++] = indicesCount+2;
        rm.indices[indI++] = indicesCount+2;
        rm.indices[indI++] = indicesCount+3;
        rm.indices[indI++] = indicesCount;
        indicesCount += 4;
    }

    public Mesh getMesh(){
        verI = 0;
        indI = 0;
        Mesh m = loader.loadToVAO(rm.vertexData, rm.indices);
        rm.clear();
        return m;
    }

    public void freeMemory(){
        loader.cleanUp();
    }


}
