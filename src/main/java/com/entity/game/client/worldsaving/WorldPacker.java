package com.entity.game.client.worldsaving;


import com.entity.game.client.misc.WorldConstants;
import com.entity.game.client.world.Chunk;
import com.entity.game.client.world.ChunkPosition;
import com.entity.game.client.voxelhandling.ChunkManager;

import java.io.*;
import java.util.HashMap;

public abstract class WorldPacker {
    public static final String WORLD_FOLDER = "worlds/";


    public static void readWorldFile(ChunkManager chunkManager, String name) throws IOException {
        File file = new File(WORLD_FOLDER+name);
        if(!file.exists()) return;
        DataInputStream dataStream = new DataInputStream(new FileInputStream(file));
        while (dataStream.available() > 0){

            ChunkPosition chunkPosition = new ChunkPosition(dataStream.readInt(), dataStream.readInt(), dataStream.readInt());

            int readByte;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            //reading one chunk of data
            long nano = System.nanoTime();
            int b = 0;
            while (dataStream.available() > 0){

                readByte = dataStream.readByte();
                if(readByte == -1) break;
                int type = readByte;
                int count  = dataStream.readShort();

                //data types are stored like this (count:type)
                for(int c = 0; c < count; c++){
                    outputStream.write(type);
                }
                b++;
            }
            System.out.println("took " + (System.nanoTime() - nano) + " nano seconds to load " + b + " block segments");

            byte[] blocks = outputStream.toByteArray();
            byte[] blocksFinal = new byte[WorldConstants.CHUNKVOLUME];
            System.out.println(blocks.length);
            for(int i = 0; i < blocks.length; i++){
                blocksFinal[i] = blocks[i];
            }

            Chunk savedChunk = new Chunk(chunkPosition);
            savedChunk.setBlocks(blocksFinal);
            chunkManager.addModifiedChunk(savedChunk);

        }
    }

    public static void saveWorldFile(String name, HashMap<ChunkPosition, Chunk> map) throws IOException {
        File folder = new File(WORLD_FOLDER);
        File file = new File(folder, name);
        if(!folder.exists()) {
            folder.mkdirs();
            file.createNewFile();
        }
        FileOutputStream fileStream = new FileOutputStream(file);
        DataOutputStream dataStream = new DataOutputStream(fileStream);
        for(ChunkPosition chunkPos : map.keySet()){
            System.out.println("writing " + chunkPos.toString());
            dataStream.writeInt(chunkPos.x);
            dataStream.writeInt(chunkPos.y);
            dataStream.writeInt(chunkPos.z);
            Chunk c = map.get(chunkPos);
            byte lastBlock = -1;
            int sameBlockCounter = 0;
            ByteArrayOutputStream blockData = new ByteArrayOutputStream();
            DataOutputStream blockDataStream = new DataOutputStream(blockData);
            for(byte b : c.getBlocks()){
                if(b == lastBlock || lastBlock == -1){
                    lastBlock = b;
                    sameBlockCounter++;
                    continue;
                }
                else {
                    //block has changed, write block data to stream
                    blockDataStream.writeByte(lastBlock);
                    blockDataStream.writeShort(sameBlockCounter);
                    lastBlock = b;
                    sameBlockCounter = 1;
                }
            }
            byte[] ba = blockData.toByteArray();
            System.out.println("ba " + ba.length);
            dataStream.write(ba);
            dataStream.writeByte(-1); //Terminator
        }
    }
}
