package com.entity.game.client.physics;

public class AABB {
    public static boolean AABBtoAABB(Hitbox tBox1, Hitbox tBox2) {
        return(tBox1.m_vecMax.x > tBox2.m_vecMin.x &&
                tBox1.m_vecMin.x < tBox2.m_vecMax.x &&
                tBox1.m_vecMax.y > tBox2.m_vecMin.y &&
                tBox1.m_vecMin.y < tBox2.m_vecMax.y &&
                tBox1.m_vecMax.z > tBox2.m_vecMin.z &&
                tBox1.m_vecMin.z < tBox2.m_vecMax.z);
    }
}
