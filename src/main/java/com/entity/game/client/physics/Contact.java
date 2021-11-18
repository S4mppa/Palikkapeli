package com.entity.game.client.physics;

import org.joml.Vector3f;

public class Contact {
    public boolean isIntersecting;
    public Vector3f nEnter; //Minimum translation vector
    public float penetration;
}
