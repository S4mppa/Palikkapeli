package com.entity.game.client.voxelhandling;


import com.entity.game.client.misc.BlockData;
import com.entity.game.client.misc.Coordinates;
import com.entity.game.client.misc.WorldConstants;
import com.entity.game.client.events.BlockBreakEvent;
import com.entity.game.client.events.BlockPlaceEvent;
import com.entity.game.client.events.EventBus;
import com.entity.game.client.world.*;


import java.util.*;

public class ChunkManager {
    private ChunkMeshResourceManager opaqueResourceManager = new ChunkMeshResourceManager();
    private ChunkMeshResourceManager fluidResourceManager = new ChunkMeshResourceManager();

    private final int CHUNKSIZE = WorldConstants.CHUNKSIZE;
    private final ArrayDeque<Chunk> chunkUpdateQueue;
    private final ArrayDeque<BlockUpdate> blockUpdateQueue;
    private final ArrayDeque<Chunk> chunkUnloadQueue;
    private final ArrayDeque<Chunk> meshGenQueue;
    private final ArrayDeque<Chunk> chunkAddQueue;
    private final ArrayDeque<Chunk> chunkDelQueue;
    private final HashMap<Integer, BlockData> blockDataMap;
    private final HashMap<ChunkPosition, Chunk> activeChunks;

    private final ArrayList<Chunk> renderArray;
    private boolean chunkGenFinished = false;
    private final EventBus eventBus;

    private final HashMap<ChunkPosition, Chunk> modifiedChunks;

    private World world;

    public ChunkManager(World world, EventBus eventBus) {
        chunkUpdateQueue = new ArrayDeque<>();
        blockUpdateQueue = new ArrayDeque<>();
        chunkUnloadQueue = new ArrayDeque<>();
        chunkAddQueue = new ArrayDeque<>();
        chunkDelQueue = new ArrayDeque<>();
        meshGenQueue = new ArrayDeque<>();
        renderArray = new ArrayList<>();
        this.eventBus = eventBus;

        activeChunks = new HashMap<>();
        blockDataMap = new HashMap<>();
        modifiedChunks = new HashMap<>();
        addBlockData();
        this.world = world;
    }
    public void addBlockData(){
        for(BlockData blockData : BlockData.values()){
            blockDataMap.put(blockData.getBlockID(), blockData);
        }
    }

    public boolean chunkSaved(ChunkPosition chunkPosition){
        return modifiedChunks.containsKey(chunkPosition);
    }

    public Chunk getModifiedChunk(ChunkPosition chunkPosition){
        return modifiedChunks.get(chunkPosition);
    }

    public void addModifiedChunk(Chunk chunk){
        modifiedChunks.put(chunk.getOrigin(), chunk);
    }

    public HashMap<ChunkPosition, Chunk> getModifiedChunks() {
        return modifiedChunks;
    }

    public void setChunkGenFinished(boolean state){
        chunkGenFinished = state;
    }

    public ArrayDeque<Chunk> getChunkUpdateList() {
        return chunkUpdateQueue;
    }

    public void addChunkUpdate(Chunk chunk) {
        if(chunkUpdateQueue.contains(chunk)) return;
        chunkUpdateQueue.add(chunk);

    }

    public void addBlockUpdate(BlockUpdate update) {
        blockUpdateQueue.add(update);
    }

    public int getBlockAt(BlockPosition pos) {
        ChunkPosition cpos = Coordinates.getChunkOriginFromBlock(pos.x, pos.y, pos.z);
        ChunkPosition chunkPos = new ChunkPosition(cpos.x * CHUNKSIZE, cpos.y * CHUNKSIZE, cpos.z * CHUNKSIZE);
        if (!activeChunks.containsKey(chunkPos)) return 0;
        int blockType = activeChunks.get(chunkPos).getBlockAt(Coordinates.toLocalBlockPosition(pos));
        if(!blockDataMap.get(blockType).isCollidable()) return 0;
        return blockType;
    }

    public int getAnyBlockAt(BlockPosition pos) {
        ChunkPosition cpos = Coordinates.getChunkOriginFromBlock(pos.x, pos.y, pos.z);
        ChunkPosition chunkPos = new ChunkPosition(cpos.x * CHUNKSIZE, cpos.y * CHUNKSIZE, cpos.z * CHUNKSIZE);
        if (!activeChunks.containsKey(chunkPos)) return 0;
        return activeChunks.get(chunkPos).getBlockAt(Coordinates.toLocalBlockPosition(pos));
    }

    public int getBlockAt(int[] coords) {
        ChunkPosition cpos = Coordinates.getChunkOriginFromBlock(coords[0], coords[1], coords[2]);
        ChunkPosition chunkPos = new ChunkPosition(cpos.x * CHUNKSIZE, cpos.y * CHUNKSIZE, cpos.z * CHUNKSIZE);
        if (!activeChunks.containsKey(chunkPos)) return 0;
        int[] bpos = Coordinates.toLocalBlockPosition(coords[0], coords[1], coords[2]);
        return activeChunks.get(chunkPos).getBlockAt(bpos[0], bpos[1], bpos[2]);
    }

    public BlockData getBlockDataAt(BlockPosition pos){
        return blockDataMap.get(getBlockAt(pos));
    }

    public Chunk getChunkAt(ChunkPosition chunkPosition) {
        if (chunkExists(chunkPosition)) {
            return activeChunks.get(chunkPosition);
        }
        return null;
    }

    public void deleteBlock(Chunk chunk, BlockPosition bp) {
        BlockPosition block = Coordinates.toLocalBlockPosition(bp);
        int type = chunk.getBlockAt(block);
        if (type == 0) return;
        eventBus.send(new BlockBreakEvent(world, blockDataMap.get(type), bp, chunk));
        chunk.replaceBlockType(block, 0);
        chunk.setUpdateState(true);
        updateNeighbours(chunk, block);
        addChunkUpdate(chunk);
    }

    public void updateNeighbours(Chunk chunk, BlockPosition localP){
        if(localP.y == CHUNKSIZE-1){
            updateNeighbour(chunk, 0);
        }
        else if(localP.y == 0){
            updateNeighbour(chunk, 1);
        }
        if(localP.x == 0){
            updateNeighbour(chunk, 2);

        }
        else if(localP.x == CHUNKSIZE-1){
            updateNeighbour(chunk, 3);

        }
        if(localP.z == 0){
            updateNeighbour(chunk, 4);
        }
        else if(localP.z == CHUNKSIZE-1){
            updateNeighbour(chunk, 5);
        }

    }

    public void updateNeighbour(Chunk chunk, int index){
        Chunk c = getChunkAt(chunk.getNeighbours()[index]);
        if(c == null) return;
        if(!c.hasMesh()) return;
        if(!c.isActive()) return;
        c.setUpdateState(true);
        addChunkUpdate(c);
    }

    public void addBlock(Chunk chunk, BlockPosition bp, int blockType) {
        BlockPosition block = Coordinates.toLocalBlockPosition(bp);
        chunk.replaceBlockType(block, blockType);
        chunk.setUpdateState(true);
        addChunkUpdate(chunk);
        eventBus.send(new BlockPlaceEvent(world, blockDataMap.get(blockType), bp, chunk));
    }

    public void handleBlockUpdate(BlockUpdate update) {
        boolean createChunk = true;
        BlockPosition pos = update.getBlockPos();
        ChunkPosition cpos = Coordinates.getChunkOriginFromBlock(pos.x, pos.y, pos.z);
        ChunkPosition chunkPos = new ChunkPosition(cpos.x * CHUNKSIZE, cpos.y * CHUNKSIZE, cpos.z * CHUNKSIZE);
        Chunk chunk = activeChunks.get(chunkPos);
        if(chunk == null) return;
        int type = chunk.getBlockAt(Coordinates.toLocalBlockPosition(pos));
        if (update.w == 0 && type >= 1) {
            deleteBlock(chunk, pos);
            createChunk = false;
        } else if (update.w >= 1 && !blockDataMap.get(type).isCollidable()) {
            addBlock(chunk, pos, update.w);
            createChunk = false;
        }

    }

    public void blockUpdates() {
        Iterator<BlockUpdate> blockUpdateIterator = blockUpdateQueue.iterator();
        while (blockUpdateIterator.hasNext()){
            BlockUpdate update = blockUpdateIterator.next();
            blockUpdateIterator.remove();
            handleBlockUpdate(update);
        }
    }

    public synchronized Collection<Chunk> getAllChunks(){
        try{
            return new ArrayList<>(renderArray);
        }
        catch (ConcurrentModificationException e){
            return null;
        }
    }

    public boolean chunkExists(ChunkPosition cp) {
        return activeChunks.containsKey(cp);
    }

    public boolean hasChunk(ChunkPosition chunkPosition) {
        return activeChunks.containsKey(chunkPosition);
    }

    public ArrayList<Chunk> getRenderArray(){
        return renderArray;
    }

    public boolean hasChunk(int x, int y, int z) {
        return hasChunk(new ChunkPosition(x, y, z));
    }

    public Chunk getChunkAt(int x, int y, int z){
        return getChunkAt(new ChunkPosition(x,y,z));
    }

    public boolean hasNeighbours(ChunkPosition cp) {

        return hasChunk(cp) &&
                //top
                hasChunk(cp.x, cp.y + CHUNKSIZE, cp.z) &&
                hasChunk(cp.x, cp.y - CHUNKSIZE, cp.z) &&
                hasChunk(cp.x - CHUNKSIZE, cp.y, cp.z) &&
                hasChunk(cp.x + CHUNKSIZE, cp.y, cp.z - CHUNKSIZE) &&
                hasChunk(cp.x, cp.y, cp.z + CHUNKSIZE);
    }

    public void setChunkNeighbours(Chunk chunk){
        ChunkPosition cp = chunk.getOrigin();

        ChunkPosition top = new ChunkPosition(cp.x, cp.y + CHUNKSIZE, cp.z);
        ChunkPosition bottom = new ChunkPosition(cp.x, cp.y - CHUNKSIZE, cp.z);
        ChunkPosition left = new ChunkPosition(cp.x - CHUNKSIZE, cp.y, cp.z);
        ChunkPosition right = new ChunkPosition(cp.x + CHUNKSIZE, cp.y, cp.z);
        ChunkPosition front = new ChunkPosition(cp.x, cp.y, cp.z - CHUNKSIZE);
        ChunkPosition back = new ChunkPosition(cp.x, cp.y, cp.z + CHUNKSIZE);
        ChunkPosition[] array = {top,bottom,left,right,front,back};
        for(int i = 0; i < array.length; i++){
            if(array[i] != null){
                chunk.setNeighbour(array[i], i);
            }
        }
    }



    public void update() {
        for(int i = 0; i < 2; i++){
            while (!chunkAddQueue.isEmpty()){
                Chunk c = chunkAddQueue.remove();
                activeChunks.put(c.getOrigin(), c);
            }
            while (!chunkDelQueue.isEmpty()){
                Chunk c = chunkDelQueue.remove();
                activeChunks.entrySet().removeIf(c1 -> c1.getValue().equals(c));
            }
            if(!chunkUpdateQueue.isEmpty()){
                Chunk c = chunkUpdateQueue.remove();
                if (!c.hasMesh() && c.isActive()) {
                    if(!chunkExists(c.getOrigin())) return;
                    ChunkMeshGeneration.generateChunkMesh(c, this, blockDataMap, opaqueResourceManager, fluidResourceManager);
                    c.setMesh();
                    renderArray.add(c);
                }
                else if (c.shouldUpdate() && c.isActive() && c.hasMesh()) {
                    c.setRenderState(true);
                    c.getChunkMesh().freeMemory();
                    c.getFluidChunkMesh().freeMemory();
                    ChunkMeshGeneration.generateChunkMesh(c, this, blockDataMap, opaqueResourceManager, fluidResourceManager);
                    c.setMesh();
                    c.setUpdateState(false);
                }
            }
        }
        updateUnload();
        blockUpdates();
    }

    public List<Chunk> getActiveChunks(){
        return new ArrayList<>(activeChunks.values());
    }

    public void unloadChunk(Chunk c){
        if(chunkUnloadQueue.contains(c)) return;
        chunkUnloadQueue.add(c);
    }

    public void addGlobalBlock(int[] b, int type, ChunkPosition og){
        ChunkPosition cpos = Coordinates.getChunkOriginFromBlock(b[0],b[1],b[2]);
        Chunk chunk = getChunkAt(cpos.x*WorldConstants.CHUNKSIZE,cpos.y*WorldConstants.CHUNKSIZE,cpos.z*WorldConstants.CHUNKSIZE);
        if(chunk != null){
            chunk.replaceBlockType(Coordinates.toLocalBlockPosition(b[0],b[1],b[2]), type);
        }
    }

    public void removeChunk(Chunk c){
        if(c.isActive()){
            if(c.getChunkMesh() != null)  c.getChunkMesh().freeMemory(); //Without this the program will run out of native memory
            if(c.getFluidChunkMesh() != null) {
                c.getFluidChunkMesh().freeMemory();
            }
            removeChunkFromActiveList(c);
            renderArray.remove(c);
        }
        c.freeBlockArray();
    }

    public void updateUnload(){
        for(int i = 0; i < chunkUnloadQueue.size(); i++){
            Chunk c = chunkUnloadQueue.remove();
            for(int y = 0; y < 256; y+=32){
                ChunkPosition cpos = new ChunkPosition(c.getOrigin().x, y, c.getOrigin().z);
                if(!chunkExists(cpos)) continue;
                removeChunk(getChunkAt(cpos));
            }
        }

    }

    public synchronized void removeChunkFromActiveList(Chunk chunk){
        chunkDelQueue.add(chunk);
    }

    public void updateActivity(Chunk chunk){

    }

    public void addActiveChunk(Chunk chunk){
        chunkAddQueue.add(chunk);
    }

    public void addChunk(Chunk chunk){
        if(activeChunks.containsKey(chunk.getOrigin())) return;
        activeChunks.put(chunk.getOrigin(), chunk);
    }
}
