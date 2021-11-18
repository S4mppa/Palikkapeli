package com.entity.game.client.textures;


import com.entity.game.client.renderEngine.Loader;
import org.lwjgl.opengl.*;


import static org.lwjgl.glfw.GLFW.glfwExtensionSupported;

public class TextureArray {
    private int textureID;
    private static final int layerCount = 23+1; //+1 since air isnt a block
    private static final int textureSize = 16;
    public static final String PATH = "blocks/x16/";

    public TextureArray(){
        loadToGPU();
    }

    public int getID(){
        return textureID;
    }

    public void loadToLayer(String fileName, int layer){
        TextureData file = Loader.loadToBuffer(PATH + fileName);
        GL45.glTexSubImage3D(GL45.GL_TEXTURE_2D_ARRAY, 0, 0, 0, layer, file.getWidth(), file.getHeight(), 1, GL45.GL_RGBA, GL45.GL_UNSIGNED_BYTE, file.getBuffer());
    }

    public void loadToGPU(){

        int textureID = GL30.glGenTextures();
        this.textureID = textureID;
        GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, textureID);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT);
        GL30.glTexImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, GL30.GL_RGBA, textureSize, textureSize,
                layerCount, 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, 0);

        loadToLayer("stone.png", 6);
        loadToLayer("grass_block_top.png", 1);
        loadToLayer("dirt.png", 3);
        loadToLayer("snow.png", 4);
        loadToLayer("grass_block_side.png", 5);
        loadToLayer("dirt.png", 2);
        loadToLayer("sand.png", 7);
        loadToLayer("grass_block_snow.png", 8);
        loadToLayer("cobblestone.png", 9);
        loadToLayer("spruce_log.png", 10);
        loadToLayer("spruce_log_top.png", 11);
        loadToLayer("leaves.png", 12);
        loadToLayer("oak_planks.png", 13);
        loadToLayer("glass.png", 14);

        loadToLayer("cactus_top.png", 15);
        loadToLayer("cactus_side.png", 16);
        loadToLayer("cactus_top.png", 17);
        loadToLayer("grass.png", 18);
        loadToLayer("flower_rose.png", 19);
        loadToLayer("dark_leaves.png", 20);
        loadToLayer("snowy_leaves.png", 21);
        loadToLayer("snowy_leaves_top.png", 22);
        loadToLayer("ice.png", 23);

        GL45.glGenerateMipmap(GL45.GL_TEXTURE_2D_ARRAY);
        GL45.glTexParameteri(GL45.GL_TEXTURE_2D_ARRAY, GL45.GL_TEXTURE_MIN_FILTER,
                GL45.GL_LINEAR_MIPMAP_LINEAR);
        GL45.glTexParameteri(GL45.GL_TEXTURE_2D_ARRAY, GL45.GL_TEXTURE_MAG_FILTER,
                GL45.GL_NEAREST);
        GL45.glTexParameterf(GL45.GL_TEXTURE_2D_ARRAY, GL45.GL_TEXTURE_LOD_BIAS, 0);
    }
}
