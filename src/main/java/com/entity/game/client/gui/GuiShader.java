package com.entity.game.client.gui;

import com.entity.game.client.shaders.ShaderProgram;
import org.joml.Matrix4f;

public class GuiShader extends ShaderProgram {
    private static final String VERTEX_FILE = "gui/GuiVertexShader.txt";
    private static final String FRAGMENT_FILE = "gui/GuiFragmentShader.txt";

    public GuiShader() throws Exception{
        super(VERTEX_FILE, FRAGMENT_FILE);
        createUniform("transformationMatrix");
        createUniform("shadeColour");
    }

    public void loadTransformation(Matrix4f matrix){
        setUniform("transformationMatrix", matrix);
    }


}
