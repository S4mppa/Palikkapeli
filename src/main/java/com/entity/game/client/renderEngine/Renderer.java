package com.entity.game.client.renderEngine;



import com.entity.game.client.misc.WorldConstants;
import org.lwjgl.opengl.GL30;


public abstract class Renderer {
    public void clear(){
        GL30.glClearColor(WorldConstants.SKYCOLOUR.x, WorldConstants.SKYCOLOUR.y, WorldConstants.SKYCOLOUR.z,1);
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT );
    }
}
