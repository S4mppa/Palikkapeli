package com.entity.game.client.textRendering;

import com.entity.game.client.shaders.ShaderProgram;
import org.joml.Vector2f;
import org.joml.Vector3f;


public class FontShader extends ShaderProgram {

    private static final String VERTEX_FILE = "text/fontVertex.txt";
    private static final String FRAGMENT_FILE = "text/fontFragment.txt";

    public FontShader() throws Exception{
        super(VERTEX_FILE, FRAGMENT_FILE);
        createUniform("colour");
        createUniform("translation");
        createUniform("fontAtlas");
    }
    protected void loadColour(Vector3f colour){
        super.setUniform("colour", colour);
    }
    protected void loadTranslation(Vector2f translation){
        super.setUniform("translation", translation);
    }
}
