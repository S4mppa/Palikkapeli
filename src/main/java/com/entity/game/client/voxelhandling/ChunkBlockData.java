package com.entity.game.client.voxelhandling;



import com.entity.game.client.misc.WorldConstants;

import java.util.Arrays;
import java.util.HashMap;

public class ChunkBlockData {
    private int arraySize = 0;
    private byte[][] blockData;
    private HashMap<Integer, Boolean> freeMap;

    public ChunkBlockData(){
        blockData = new byte[arraySize][WorldConstants.CHUNKVOLUME];
        freeMap = new HashMap<>();
        fillHashMap();
    }

    public void fillHashMap(){
        for(int i = 0; i < arraySize; i++){
            freeMap.put(i, false);
        }
    }

    public void setReserved(int arrayIndex, boolean reserved){
        freeMap.put(arrayIndex, reserved);
    }

    public void clearArray(int arrayIndex){
        Arrays.fill(blockData[arrayIndex], (byte) 0);
        freeMap.put(arrayIndex, false);
    }

    public BlockReturnInfo getFreeArray(){
        for(int i = 0; i < arraySize; i++){
            if(freeMap.containsKey(i) && !freeMap.get(i)){
                setReserved(i, true);
                return new BlockReturnInfo(i, blockData[i]);
            }
        }
        System.out.println("\u001B[31m" + "No free arrays left! [ChunkBlockData]");
        return null;
    }
}
