package com.entity.game.client;


import com.entity.game.client.misc.Utils;
import com.entity.game.client.events.*;
import com.entity.game.client.world.World;
import com.entity.game.client.renderEngine.CameraManager;
import com.entity.game.client.user.ConfigManager;
import com.entity.game.client.worldsaving.WorldPacker;

public class MainListener implements Listener {

    public MainListener(EventBus eventBus){
        eventBus.subscribe(BlockBreakEvent.class, this::onBreak);
        eventBus.subscribe(BlockPlaceEvent.class, this::onPlace);
        eventBus.subscribe(GameCloseEvent.class, this::onClose);
    }

    public void onClose(GameCloseEvent event){
        World world = Main.player.getWorld();
        ConfigManager.user.setProperty("last-pos", Utils.vectorToString(CameraManager.getActiveCamera().getPosition()));
        try {
            WorldPacker.saveWorldFile(world.getName()+".world", world.getChunkManager().getModifiedChunks());
            ConfigManager.saveAll();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onPlace(BlockPlaceEvent event){
        event.getWorld().getChunkManager().addModifiedChunk(event.getChunk());
    }

    public void onBreak(BlockBreakEvent event){
        event.getWorld().getChunkManager().addModifiedChunk(event.getChunk());
        System.out.println("Block break at: " + event.getBlockPosition());
    }
}
