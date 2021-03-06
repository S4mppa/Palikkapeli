package com.entity.game.client.textRendering;


import java.util.List;
import java.util.Map;

import com.entity.game.client.textMesh.FontType;
import com.entity.game.client.textMesh.GUIText;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;


public class FontRenderer {
    private FontShader shader;

    public FontRenderer() {
        try {
            shader = new FontShader();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void render(Map<FontType, List<GUIText>> texts){
        prepare();
        for(FontType font : texts.keySet()){
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
            for(GUIText text : texts.get(font)){
                renderText(text);
            }
        }
        endRendering();
    }

    public void cleanUp(){
        shader.cleanUp();
    }

    private void prepare(){
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        shader.start();
    }

    private void renderText(GUIText text){
        GL30.glBindVertexArray(text.getMesh());
        shader.setUniform("fontAtlas", 0);
        shader.loadColour(text.getColour());
        shader.loadTranslation(text.getPosition());
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());
        GL30.glBindVertexArray(0);
    }

    private void endRendering(){
        shader.unbind();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
}
