package com.entity.game.client.water;


import com.entity.game.client.shaders.ShaderProgram;

public class UnderWaterShader extends ShaderProgram {

    public UnderWaterShader() throws Exception {
        super("water/uWaterV.txt", "water/uWaterF.txt");
        createUniform("transformationMatrix");
        createUniform("dudvMap");
        createUniform("depthMap");
        createUniform("time");
    }
}
