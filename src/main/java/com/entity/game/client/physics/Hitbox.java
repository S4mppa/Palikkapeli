package com.entity.game.client.physics;

import org.joml.Vector3f;

public class Hitbox {
    public Vector3f m_vecMax;
    public Vector3f m_vecMin;

    public Hitbox(Vector3f m_vecMin, Vector3f m_vecMax){
        this.m_vecMin = m_vecMin;
        this.m_vecMax = m_vecMax;
    }
}
