package com.entity.game.client.voxelhandling;


import com.entity.game.client.misc.DisplayTime;
import com.entity.game.client.misc.FrustumCullingFilter;
import com.entity.game.client.misc.WorldConstants;
import com.entity.game.client.textures.TextureArray;
import com.entity.game.client.world.Chunk;
import com.entity.game.client.world.ChunkPosition;
import com.entity.game.client.world.Entity;
import com.entity.game.client.world.Player;
import com.entity.game.client.renderEngine.Camera;
import com.entity.game.client.renderEngine.CameraManager;
import com.entity.game.client.renderEngine.Renderer;
import com.entity.game.client.renderEngine.Transformation;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;


public class ChunkRenderer extends Renderer {

    public TextureArray textureArrayAtlas;

    private FrustumCullingFilter frustumCullingFilter;
    private ChunkShader shader;
    private Player player;

    public ChunkRenderer(Player player, FrustumCullingFilter filter) throws Exception{
        this.player = player;
        this.shader = new ChunkShader();

        textureArrayAtlas = new TextureArray();
        frustumCullingFilter = filter;
    }


    public void render(Vector4f planeClip){
        shader.start();
        shader.setUniform("plane", planeClip);
        shader.setUniform("skyColour", WorldConstants.SKYCOLOUR);
        Matrix4f projectionMatrix = Transformation.getProjectionMatrix(WorldConstants.FOV, WorldConstants.WINDOW_WIDTH, WorldConstants.WINDOW_HEIGHT, WorldConstants.Z_NEAR, WorldConstants.Z_FAR);

        shader.setUniform("projectionMatrix", projectionMatrix);
        shader.setUniform("texture_sampler", 0);
        shader.setUniform("isEyeInWater", player.isInWater());
        Entity chunkEntity = new Entity();

        // Update view Matrix
        Camera camera = CameraManager.getActiveCamera();
        Matrix4f viewMatrix = Transformation.getViewMatrix(camera);
        shader.setUniform("viewMatrix", viewMatrix);
        shader.setUniform("cameraPos", camera.getPosition());
        shader.setUniform("time", DisplayTime.getTime());
        shader.setUniform("worldTime", player.getWorld().getNormalizedTime());
        shader.setUniform("sunDir", player.getWorld().getSunDir());
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        // Bind the texture
        GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, textureArrayAtlas.getID());
        frustumCullingFilter.updateFrustum(projectionMatrix, viewMatrix);
        ChunkManager chunkManager = player.getWorld().getChunkManager();

        for(Chunk chunk : chunkManager.getRenderArray()){
            shader.setUniform("perChunkTime", (float) chunk.animTime);
            ChunkPosition cpos = chunk.getOrigin();
            if(!chunk.isToBeRendered()) continue;
            if(!frustumCullingFilter.insideFrustum(cpos.x,cpos.y,cpos.z)) continue;
            if(cpos.dist(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z) > WorldConstants.RENDER_DISTANCE *WorldConstants.CHUNKSIZE) continue;
            if(chunk.hasFlowers()) GL30.glDisable(GL30.GL_CULL_FACE);
            ChunkPosition cp = chunk.getOrigin();
            chunkEntity.setPosition(cp.x - camera.getPosition().x,cp.y - camera.getPosition().y,cp.z - camera.getPosition().z);
            Matrix4f modelViewMatrix = Transformation.getTransformation(chunkEntity);

            shader.setUniform("grassColor", chunk.getBiome().getGrassColor().getColor());
            shader.setUniform("transformationMatrix", modelViewMatrix);
            chunk.getMesh().render();
            if(chunk.hasFlowers()) GL30.glEnable(GL30.GL_CULL_FACE);
        }

        shader.unbind();
    }
}


