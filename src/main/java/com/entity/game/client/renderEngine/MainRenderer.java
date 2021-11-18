package com.entity.game.client.renderEngine;


import com.entity.game.client.gui.GuiRenderer;
import com.entity.game.client.misc.FrustumCullingFilter;
import com.entity.game.client.misc.Utils;
import com.entity.game.client.misc.WorldConstants;
import com.entity.game.client.skybox.AtmosRenderer;
import com.entity.game.client.skybox.SkyboxRenderer;
import com.entity.game.client.textRendering.TextMaster;
import com.entity.game.client.display.Window;
import com.entity.game.client.world.Player;
import com.entity.game.client.postProcessing.Fbo;
import com.entity.game.client.postProcessing.PostProcessing;
import com.entity.game.client.voxelhandling.ChunkRenderer;
import com.entity.game.client.water.UnderWaterRenderer;
import com.entity.game.client.water.WaterRenderer;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;


public class MainRenderer extends Renderer{
    private WaterRenderer waterRenderer;
    private SkyboxRenderer skyboxRenderer;
    private AtmosRenderer atmosRenderer;
    private ChunkRenderer chunkRenderer;
    private HighlightRenderer highlightRenderer;
    private FrustumCullingFilter frustumCullingFilter;

    private Fbo postProcessedScreen;
    private Fbo reflectionFbo;
    private Fbo refractionFbo;
    private Window window;
    private UnderWaterRenderer underWaterRenderer;
    private Player player;
    private final GuiRenderer guiRenderer;
    public MainRenderer(Player player,
                        Window window,
                        SkyboxRenderer skyboxRenderer,
                        HighlightRenderer highlightRenderer,
                        GuiRenderer guiRenderer,
                        Loader loader) throws Exception{
        frustumCullingFilter = new FrustumCullingFilter();
        this.player = player;
        this.window = window;
        this.highlightRenderer = highlightRenderer;
        this.chunkRenderer = new ChunkRenderer(player, frustumCullingFilter);
        this.skyboxRenderer = skyboxRenderer;
        this.reflectionFbo = new Fbo(1280, 720, Fbo.DEPTH_TEXTURE, window);
        this.refractionFbo = new Fbo(1280, 720, Fbo.DEPTH_TEXTURE, window);
        this.postProcessedScreen = new Fbo(window.getWidth(), window.getHeight(), Fbo.DEPTH_TEXTURE, window);
        this.waterRenderer = new WaterRenderer(player, frustumCullingFilter, reflectionFbo,refractionFbo);
        this.underWaterRenderer = new UnderWaterRenderer(loader, postProcessedScreen, reflectionFbo);
        this.guiRenderer = guiRenderer;
        this.atmosRenderer = new AtmosRenderer(loader);
    }

    public void updateFbos(){
        if(!player.isInWater()){
            Camera camera = CameraManager.getActiveCamera();
            reflectionFbo.bindFrameBuffer();
            float distance = 2 * (camera.getPosition().y - WorldConstants.WATER_LEVEL);
            camera.getPosition().y -= distance;
            camera.invertPitch();
            renderScene(new Vector4f(0,1,0, -WorldConstants.WATER_LEVEL-0.1f), true, false);
            camera.getPosition().y += distance;
            camera.invertPitch();
            reflectionFbo.unbindFrameBuffer();
            refractionFbo.bindFrameBuffer();
            renderScene(new Vector4f(0,-1,0, WorldConstants.WATER_LEVEL-0.01f), true, false);
            refractionFbo.unbindFrameBuffer();
        }
        else {
            reflectionFbo.bindFrameBuffer();
            renderScene(new Vector4f(0,-1,0, WorldConstants.WATER_LEVEL+1f), true, true);
            reflectionFbo.unbindFrameBuffer();
            refractionFbo.bindFrameBuffer();
            renderScene(new Vector4f(0,1,0, -WorldConstants.WATER_LEVEL-0.01f), true, true);
            refractionFbo.unbindFrameBuffer();
        }
    }

    public void update(){
        updateFbos();
    }

    public void renderMenuScreen(){
        window.swapBuffers();
        guiRenderer.render();
        TextMaster.render();
    }

    public void render(){
        window.swapBuffers();

        postProcessedScreen.bindFrameBuffer();
        renderScene(new Vector4f(0,0,0, 0), false, false);
        postProcessedScreen.unbindFrameBuffer();
        PostProcessing.doPostProcessing(postProcessedScreen.getColourTexture(), postProcessedScreen.getDepthTexture());
        if(player.isInWater()){
            underWaterRenderer.render();
        }
        if(!WorldConstants.NO_GUI) {
            TextMaster.render();
            guiRenderer.render();
        }
    }

    public void renderScene(Vector4f planeClip, boolean isFboUpdate, boolean isUnderWater){
        chunkRenderer.clear();
        GL30.glDepthMask(false);
        skyboxRenderer.render(player.getWorld());
        //atmosRenderer.render();
        GL30.glDepthMask(true);
        chunkRenderer.render(planeClip);
        if(!isFboUpdate || isUnderWater) waterRenderer.render(planeClip, !isUnderWater);
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glCullFace(GL30.GL_FRONT);
        if(highlightRenderer.getLatestPoint() != null && !isFboUpdate){
            highlightRenderer.render(Utils.blockPosToVector(highlightRenderer.getLatestPoint()));
        }
        GL30.glCullFace(GL30.GL_BACK);
        GL30.glDisable(GL30.GL_BLEND);
    }


}
