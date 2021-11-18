package com.entity.game.client.world;



import com.entity.game.client.misc.Coordinates;
import com.entity.game.client.misc.WorldConstants;
import com.entity.game.client.voxelhandling.Biomes;
import com.entity.game.client.voxelhandling.BlockReturnInfo;
import com.entity.game.client.voxelhandling.ChunkBlockData;
import com.entity.game.client.voxelhandling.ChunkManager;

import java.util.Objects;

public class Chunk {
    private final int CHUNKSIZE = WorldConstants.CHUNKSIZE;
    public double animTime = 0;
    private Biomes biome;
    private ChunkPosition[] neighbours;
    private ChunkPosition origin;
    private ChunkMesh chunkMesh;
    private ChunkMesh fluidMesh;
    private Mesh mesh;
    private Mesh fMesh;
    private byte[] blocks;
    private boolean hasMesh = false;
    private boolean chunkMeshGenerated = false;
    private boolean updateMesh = false;
    private boolean toBeRendered = true;
    private boolean active = false;
    private boolean hasFluid = false;
    private boolean hasFlowers;
    private BlockReturnInfo blockReturnInfo;
    private ChunkBlockData blockData;
    public boolean hasBeenUpdated = false;
    private int blockAmount = 0;

    public Chunk(ChunkPosition origin){
        this.origin = origin;
        this.blockData = blockData;
        this.neighbours = new ChunkPosition[6];
        this.blocks = new byte[WorldConstants.CHUNKVOLUME];
    }


    public void setBiome(Biomes biome) {
        this.biome = biome;
    }

    public Biomes getBiome() {
        return biome;
    }

    public void freeBlockArray(){

    }

    public void setBlocks(byte[] blocks) {
        this.blocks = blocks;
    }

    public void setHasFlowers(boolean value){
        this.hasFlowers = value;
    }

    public boolean hasFlowers() {
        return hasFlowers;
    }

    public ChunkPosition[] getNeighbours(){
        return neighbours;
    }

    public void setNeighbour(ChunkPosition chunk, int index){
        this.neighbours[index] = chunk;
    }

    public ChunkMesh getFluidChunkMesh(){
        return fluidMesh;
    }

    public void setHasFluid(boolean fluid){
        this.hasFluid = fluid;
    }

    public boolean hasFluid(){
        return hasFluid;
    }

    public void setBlockAmount(int amount){
        blockAmount = amount;
    }

    public int getBlockAmount(){
        return blockAmount;
    }

    public void setActive(boolean state){
        active = state;
    }

    public boolean isActive(){
        return active;
    }

    public boolean isToBeRendered(){
        return toBeRendered;
    }

    public void setRenderState(boolean state){
        this.toBeRendered = state;
    }

    public void setUpdateState(boolean state){
        this.updateMesh = state;
    }

    public boolean shouldUpdate(){
        return updateMesh;
    }

    public boolean isChunkMeshGenerated(){
        return chunkMeshGenerated;
    }

    public void setChunkMesh(ChunkMesh chunkMesh){
        this.chunkMesh = chunkMesh;
        chunkMeshGenerated = true;
    }

    public boolean hasMesh(){
        return this.hasMesh;
    }

    public ChunkMesh getChunkMesh(){
        return chunkMesh;
    }

    public Mesh getMesh(){
        return mesh;
    }

    public void setMesh(){
        this.mesh = chunkMesh.getMesh();
        this.fMesh = fluidMesh.getMesh();
        this.hasMesh = true;
    }

    public void setFluidMesh(ChunkMesh fluidMesh){
        this.fluidMesh = fluidMesh;
    }

    public Mesh getFluidMesh(){
        return fMesh;
    }

    public ChunkPosition getOrigin(){
        return origin;
    }

    public void replaceBlockType(BlockPosition blockPosition, int type){
        if(type == 0) blockAmount--;
        else blockAmount++;
        setActive(true);
        blocks[Coordinates.toLocalBlockIndex(blockPosition)] = (byte)type;
    }
    public void replaceBlockType(int[] b, int type){
        if(type == 0) blockAmount--;
        else blockAmount++;
        setActive(true);
        blocks[Coordinates.toLocalBlockIndex(b[0],b[1],b[2])] = (byte)type;
    }
    public void safeReplace(BlockPosition blockPosition, int type, ChunkManager m){
        if(safeBlockGet(blockPosition, m) != -1){
            blocks[Coordinates.toLocalBlockIndex(blockPosition)] = (byte)type;
        }
    }


    public void setOrigin(ChunkPosition origin){
        this.origin = origin;
    }


    public int getBlockAt(BlockPosition pos){

        return blocks[Coordinates.toLocalBlockIndex(pos)];

    }
    public int getBlockAt(int x, int y, int z){
        return blocks[Coordinates.toLocalBlockIndex(x, y, z)];
    }
    public int getBlockAt(int[] b){
        return blocks[Coordinates.toLocalBlockIndex(b[0], b[1], b[2])];
    }

    public int safeBlockGet(BlockPosition blockPosition, ChunkManager manager){
        int CHUNK_SIZE = WorldConstants.CHUNKSIZE;
        if (blockPosition.x < 0 || blockPosition.x >= CHUNK_SIZE ||
                blockPosition.y < 0 || blockPosition.y >= CHUNK_SIZE ||
                blockPosition.z < 0 || blockPosition.z >= CHUNK_SIZE) {
            return manager.getBlockAt(
                    Coordinates.toGlobalBlockPosition(blockPosition, origin));
        }
        return getBlockAt(blockPosition);
    }

    public int safeBlockGet(int x, int y, int z, ChunkManager manager){
        int CHUNK_SIZE = WorldConstants.CHUNKSIZE;
        if (x < 0 || x >= CHUNK_SIZE ||
                y < 0 || y >= CHUNK_SIZE ||
                z < 0 || z >= CHUNK_SIZE) {
            return manager.getBlockAt(
                    Coordinates.toGlobalBlockPosition(x,y,z, origin));
        }
        return getBlockAt(x,y,z);
    }

    public boolean getUpdateState(){
        return updateMesh;
    }

    public byte[] getBlocks() {
        return blocks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chunk chunk = (Chunk) o;
        return origin.equals(chunk.origin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin);
    }
}



