package com.entity.game.client.postProcessing;


import com.entity.game.client.skybox.AtmosFilter;
import com.entity.game.client.bloom.BrightFilter;
import com.entity.game.client.bloom.CombineFilter;
import com.entity.game.client.display.Window;
import com.entity.game.client.gaussianBlur.HorizontalBlur;
import com.entity.game.client.gaussianBlur.VerticalBlur;
import com.entity.game.client.world.Mesh;
import com.entity.game.client.renderEngine.Camera;
import com.entity.game.client.renderEngine.Loader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static Mesh quad;
	private static ContrastChanger contrastChanger;
	private static BrightFilter brightFilter;
	private static HorizontalBlur hBlur;
	private static VerticalBlur vBlur;
	private static HorizontalBlur hBlur2;
	private static VerticalBlur vBlur2;
	private static CombineFilter combineFilter;
	private static AtmosFilter atmosFilter;

	private static Camera camera;

	public static void init(Loader loader, Window window, Camera camera) throws Exception{
		quad = loader.loadToVAO(POSITIONS, 2);
		//contrastChanger = new ContrastChanger(window.getWidth()/2, window.getHeight()/2, window);
		atmosFilter = new AtmosFilter(window.getWidth(), window.getHeight(), window, camera);
		brightFilter = new BrightFilter(window.getWidth()/2, window.getHeight()/2, window);
		hBlur = new HorizontalBlur(window.getWidth()/8, window.getHeight()/8, window);
		vBlur = new VerticalBlur(window.getWidth()/8, window.getHeight()/8, window);
		hBlur2 = new HorizontalBlur(window.getWidth()/2, window.getHeight()/2, window);
		vBlur2 = new VerticalBlur(window.getWidth()/2, window.getHeight()/2, window);
		combineFilter = new CombineFilter();
	}
	
	public static void doPostProcessing(int colourTexture, int depth){
		start();
		brightFilter.render(colourTexture);
		hBlur2.render(brightFilter.getOutputTexture());
		vBlur2.render(hBlur2.getOutputTexture());
		hBlur.render(vBlur2.getOutputTexture());
		vBlur.render(hBlur.getOutputTexture());

		//atmosFilter.render(colourTexture, depth);
		combineFilter.render(colourTexture, vBlur.getOutputTexture());
		end();
	}
	
	public static void cleanUp(){
		contrastChanger.cleanUp();
		brightFilter.cleanUp();
		hBlur.cleanUp();
		vBlur.cleanUp();
		hBlur2.cleanUp();
		vBlur2.cleanUp();
		combineFilter.cleanUp();
	}
	
	private static void start(){
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	private static void end(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}


}
