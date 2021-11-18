package com.entity.game.client.world;

import org.lwjgl.opengl.GL30;

public class Mesh {
    public int vertexCount;
    public int vaoID;

    public Mesh(int vertexCount, int vaoID){
        this.vertexCount = vertexCount;
        this.vaoID = vaoID;
    }


    public void render() {
        // Draw the mesh
        GL30.glBindVertexArray(getVaoID());
        GL30.glDrawElements(GL30.GL_TRIANGLES, getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
        // Restore state
        GL30.glBindVertexArray(0);

    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getVaoID() {
        return vaoID;
    }

    public void setVertexCount(int vertexCount){
        this.vertexCount = vertexCount;
    }
}
