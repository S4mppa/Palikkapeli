package com.entity.game.client.voxelhandling;


import com.entity.game.client.shaders.ShaderProgram;

public class ChunkShader extends ShaderProgram {
    public ChunkShader() throws Exception{
        super("terrain/vertexShader.txt", "terrain/fragmentShader.txt");
        createUniform("projectionMatrix");
        createUniform("viewMatrix");
        createUniform("transformationMatrix");
        createUniform("texture_sampler");
        createUniform("lightPosition");
        createUniform("lightColour");
        createUniform("skyColour");
        createUniform("time");
        createUniform("perChunkTime");
        createUniform("plane");
        createUniform("isEyeInWater");
        createUniform("grassColor");
        createUniform("cameraPos");
        createUniform("worldTime");
        createUniform("sunDir");
    }
}
