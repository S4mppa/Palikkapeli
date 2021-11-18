package com.entity.game.client.water;


import com.entity.game.client.misc.DisplayTime;
import com.entity.game.client.misc.FrustumCullingFilter;
import com.entity.game.client.misc.WorldConstants;
import com.entity.game.client.textures.Texture;
import com.entity.game.client.world.*;
import com.entity.game.client.postProcessing.Fbo;
import com.entity.game.client.renderEngine.Camera;
import com.entity.game.client.renderEngine.CameraManager;
import com.entity.game.client.renderEngine.Renderer;
import com.entity.game.client.renderEngine.Transformation;
import com.entity.game.client.voxelhandling.ChunkManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;


public class WaterRenderer extends Renderer {
    private WaterShader waterShader;
    private Fbo reflectionFbo;
    private Fbo refractionFbo;
    private int dudvTexture;
    private int noiseTexture;
    private int normalMap;

    private FrustumCullingFilter frustumCullingFilter;

    private Player player;

    Light light = new Light(new Vector3f(-50000, 80, 0), new Vector3f(1,1,1));
    public WaterRenderer(Player player, FrustumCullingFilter filter,
                         Fbo reflectionFbo,
                         Fbo refractionFbo) throws Exception{

        this.player = player;

        this.frustumCullingFilter = filter;
        this.waterShader = new WaterShader();
        this.reflectionFbo = reflectionFbo;
        this.refractionFbo = refractionFbo;
        dudvTexture = new Texture("waterDUDV.png").getID();
        normalMap = new Texture("normalMap.png").getID();
        noiseTexture = new Texture("blueNoiseRGB.png").getID();
    }

    public void render(Vector4f planeClip, boolean setCulling){
        waterShader.start();
        Camera camera = CameraManager.getActiveCamera();
        Matrix4f viewMatrix = Transformation.getViewMatrix(camera);
        Matrix4f projectionMatrix = Transformation.getProjectionMatrix(WorldConstants.FOV, WorldConstants.WINDOW_WIDTH, WorldConstants.WINDOW_HEIGHT, WorldConstants.Z_NEAR, WorldConstants.Z_FAR);
        GL30.glActiveTexture(GL30.GL_TEXTURE1);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, reflectionFbo.getColourTexture());
        GL30.glActiveTexture(GL30.GL_TEXTURE2);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, refractionFbo.getColourTexture());
        GL30.glActiveTexture(GL30.GL_TEXTURE3);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, dudvTexture);
        GL30.glActiveTexture(GL30.GL_TEXTURE4);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, normalMap);
        GL30.glActiveTexture(GL30.GL_TEXTURE5);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, refractionFbo.getDepthTexture());
        GL30.glActiveTexture(GL30.GL_TEXTURE6);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, noiseTexture);


        waterShader.connectTextureUnits();
        waterShader.setUniform(light);
        waterShader.setUniform("plane", planeClip);
        waterShader.setUniform("skyColour", WorldConstants.SKYCOLOUR);
        waterShader.setUniform("projectionMatrix", projectionMatrix);
        waterShader.setUniform("isEyeInWater", player.isInWater());
        waterShader.setUniform("viewMatrix", viewMatrix);
        waterShader.setUniform("cameraPos", camera.getPosition());
        waterShader.setUniform("time", DisplayTime.getWaveTime());

//        //GL30.glEnable(GL30.GL_BLEND);
        ChunkManager chunkManager = player.getWorld().getChunkManager();
        if(player.isInWater()) {
            GL30.glDisable(GL30.GL_CULL_FACE);
        }
        else {
            GL30.glEnable(GL30.GL_BLEND);
        }
        Entity chunkEntity = new Entity();
        for(Chunk chunk : chunkManager.getRenderArray()){
            ChunkPosition cpos = chunk.getOrigin();
            if(!chunk.isToBeRendered()) continue;
            if(!frustumCullingFilter.insideFrustum(cpos.x,cpos.y,cpos.z)) continue;
            if(cpos.dist(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z) > WorldConstants.RENDER_DISTANCE*WorldConstants.CHUNKSIZE) continue;
            ChunkPosition cp = chunk.getOrigin();
            chunkEntity.setPosition(cp.x,cp.y,cp.z);
            Matrix4f modelViewMatrix = Transformation.getTransformation(chunkEntity);
            waterShader.setUniform("waveStrength", chunk.getBiome().getWaveStrength());
            waterShader.setUniform("transformationMatrix", modelViewMatrix);
            chunk.getFluidMesh().render();
        }
        if(player.isInWater() && setCulling) {
            GL30.glEnable(GL30.GL_CULL_FACE);
        }

        GL30.glDisable(GL30.GL_BLEND);
        waterShader.unbind();
    }
}
