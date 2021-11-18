package com.entity.game.client.world;

public class MeshFace {
    public byte[] vertices;
    public byte lightLevel;

    public MeshFace(byte[] vertices, byte lightLevel){
        this.vertices = vertices;
        this.lightLevel = lightLevel;
    }
 }
