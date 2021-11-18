package com.entity.game.client.voxelhandling;

import com.entity.game.client.misc.FrustumCullingFilter;
import com.entity.game.client.misc.Utils;
import com.entity.game.client.misc.WorldConstants;
import com.entity.game.client.world.Chunk;
import com.entity.game.client.world.ChunkPosition;
import com.entity.game.client.world.World;
import com.entity.game.client.renderEngine.Camera;
import com.entity.game.client.renderEngine.CameraManager;
import com.entity.game.client.renderEngine.Transformation;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;

public abstract class TerrainGenerator {
    private int CHUNKSIZE = WorldConstants.CHUNKSIZE;

    protected ChunkManager chunkManager;
    protected World world;

    protected final FrustumCullingFilter frustumCullingFilter = new FrustumCullingFilter();

    public TerrainGenerator(World world){
        this.world = world;
        this.chunkManager = world.getChunkManager();
    }

    public abstract int getMapHeightAt(int x, int z, Biomes biome);
    public abstract Biomes getBiome(int x, int z);

    public void tick(){
        Camera camera = CameraManager.getActiveCamera();
        frustumCullingFilter.updateFrustum(Transformation.getProjectionMatrix(WorldConstants.FOV, WorldConstants.WINDOW_WIDTH, WorldConstants.WINDOW_HEIGHT, WorldConstants.Z_NEAR, WorldConstants.Z_FAR), Transformation.getViewMatrix(camera));
        Collection<Chunk> allChunks = chunkManager.getAllChunks();
        if(allChunks!=null){

            for(Chunk chunk : allChunks){
                if(chunk == null) continue;
                ChunkPosition chunkPosition = chunk.getOrigin();
                if(CameraManager.distFromCam(chunkPosition) > WorldConstants.CHUNK_UNLOAD_DISTANCE){
                    chunkManager.unloadChunk(chunk);
                }
            }
        }

        Vector3f cp = Utils.floorVector(camera.getPosition());
        int cpx = (int) cp.x / CHUNKSIZE;
        int cpz = (int) cp.z / CHUNKSIZE;
        ArrayList<Chunk> chunks = new ArrayList<>();
        double hDistance = (Math.cos(Math.toRadians(camera.getRotation().x)));
        float theta = camera.getRotation().y;
        int cL = WorldConstants.CHUNKLOAD_DISTANCE;
        int offsetX = (int) (cL * hDistance * Math.sin(Math.toRadians(theta)));
        int offsetZ = (int) (cL * hDistance * Math.cos(Math.toRadians(theta)));
        for (int x = cpx - (cL - offsetX); x < cpx + (cL + offsetX); x++) {
            for (int y = 0; y < 256 / CHUNKSIZE; y++) {
                for (int z = cpz - (cL + offsetZ); z < cpz + (cL - offsetZ); z++) {
                    ChunkPosition cpos = new ChunkPosition(x * CHUNKSIZE, y* CHUNKSIZE, z * CHUNKSIZE);
                    if(!frustumCullingFilter.insideFrustum(cpos.x,cpos.y,cpos.z)) continue;
                    if(chunkManager.chunkExists(cpos)) continue;
                    Chunk chunk = new Chunk(cpos);
                    chunks.add(chunk);
                    chunkManager.addActiveChunk(chunk);
                }
            }
        }

        for(int i = 0; i < chunks.size(); i++){
            Chunk c = chunks.get(i);
            chunkManager.setChunkNeighbours(c);
            if(!chunkManager.chunkSaved(c.getOrigin())){
                genTerrain(c);
            }
            else {
                c.setBlocks(chunkManager.getModifiedChunk(c.getOrigin()).getBlocks());
                setChunkBiome(c);
                c.setActive(true);
            }
            chunkManager.updateActivity(c);
            if(!c.isActive()) continue;
            chunkManager.addChunkUpdate(c);
            updateNeighbourChunk(c);
        }
    }

    public void updateNeighbourChunk(Chunk c){
        chunkManager.updateNeighbour(c, 0);
        chunkManager.updateNeighbour(c, 1);
        chunkManager.updateNeighbour(c, 2);
        chunkManager.updateNeighbour(c, 3);
        chunkManager.updateNeighbour(c, 4);
        chunkManager.updateNeighbour(c, 5);
    }

    private void setChunkBiome(Chunk chunk){
        ChunkPosition chunkPosition = chunk.getOrigin();
        Biomes bbLeft = getBiome(chunkPosition.x,chunkPosition.z);
        chunk.setBiome(bbLeft);
    }


    public abstract void genTerrain(Chunk c);


}
