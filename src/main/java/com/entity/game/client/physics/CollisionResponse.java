package com.entity.game.client.physics;

import org.joml.Vector3f;

public class CollisionResponse {
    private float mtvDistance = Float.MAX_VALUE;
    private Vector3f mtvAxis = new Vector3f();
    public boolean AABBAABB(Hitbox a, Hitbox b, Contact contact)
    {
        // Minimum Translation Vector
        // ==========================
        // Set current minimum distance (max float value so next value is always less)

        // Axes of potential separation
        // ============================
        // - Each shape must be projected on these axes to test for intersection:
        //
        // (1, 0, 0)                    A0 (= B0) [X Axis]
        // (0, 1, 0)                    A1 (= B1) [Y Axis]
        // (0, 0, 1)                    A1 (= B2) [Z Axis]

        // [X Axis]
        if (!TestAxisStatic(new Vector3f(1,0,0), a.m_vecMin.x, a.m_vecMax.x, b.m_vecMin.x, b.m_vecMax.x))
            return false;

        // [Y Axis]
        if (!TestAxisStatic(new Vector3f(0,1,0), a.m_vecMin.y, a.m_vecMax.y, b.m_vecMin.y, b.m_vecMax.y))
            return false;

        // [Z Axis]
        if (!TestAxisStatic(new Vector3f(0,0,1), a.m_vecMin.z, a.m_vecMax.z, b.m_vecMin.z, b.m_vecMax.z))
            return false;



        //contact.isIntersecting = true;

        contact.isIntersecting = true;
        // Calculate Minimum Translation Vector (MTV) [normal * penetration]
        contact.nEnter = mtvAxis.normalize();

        // Multiply the penetration depth by itself plus a small increment
        // When the penetration is resolved using the MTV, it will no longer intersect
        contact.penetration = (float)Math.sqrt(mtvDistance) * 1.001f;

        return true;
    }

    private boolean TestAxisStatic(Vector3f axis, float minA, float maxA, float minB, float maxB)
    {
        // Separating Axis Theorem
        // =======================
        // - Two convex shapes only overlap if they overlap on all axes of separation
        // - In order to create accurate responses we need to find the collision vector (Minimum Translation Vector)
        // - The collision vector is made from a vector and a scalar,
        //   - The vector value is the axis associated with the smallest penetration
        //   - The scalar value is the smallest penetration value
        // - Find if the two boxes intersect along a single axis
        // - Compute the intersection interval for that axis
        // - Keep the smallest intersection/penetration value
        float axisLengthSquared = axis.dot(axis);

        // If the axis is degenerate then ignore
        if (axisLengthSquared < 1.0e-8f)
            return true;

        // Calculate the two possible overlap ranges
        // Either we overlap on the left or the right sides
        float d0 = (maxB - minA);   // 'Left' side
        float d1 = (maxA - minB);   // 'Right' side

        // Intervals do not overlap, so no intersection
        if (d0 <= 0.0f || d1 <= 0.0f)
            return false;

        // Find out if we overlap on the 'right' or 'left' of the object.
        float overlap = (d0 < d1) ? d0 : -d1;



        // The mtd vector for that axis
        Vector3f sep = axis.mul(overlap / axisLengthSquared);

        // The mtd vector length squared
        float sepLengthSquared = sep.dot(sep);

        // If that vector is smaller than our computed Minimum Translation Distance use that vector as our current MTV distance
        if (sepLengthSquared < mtvDistance)
        {
            mtvDistance = sepLengthSquared;
            mtvAxis = sep;
        }

        return true;
    }
}
