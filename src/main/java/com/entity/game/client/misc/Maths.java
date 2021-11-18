package com.entity.game.client.misc;

public class Maths {
    public static float bilinearInterpolation(float bottomLeft, float topLeft, float bottomRight,
                                float topRight, float xMin, float xMax, float zMin,
                                float zMax, float x, float z) {
        float width = xMax - xMin, height = zMax - zMin,

                xDistanceToMaxValue = xMax - x, zDistanceToMaxValue = zMax - z,

                xDistanceToMinValue = x - xMin, zDistanceToMinValue = z - zMin;

        return 1.0f / (width * height) *
                (bottomLeft * xDistanceToMaxValue * zDistanceToMaxValue +
                        bottomRight * xDistanceToMinValue * zDistanceToMaxValue +
                        topLeft * xDistanceToMaxValue * zDistanceToMinValue +
                        topRight * xDistanceToMinValue * zDistanceToMinValue);
    }

    private static float smoothstep(float edge0, float edge1, float x)
    {
        // Scale, bias and saturate x to 0..1 range
        x = x * x * (3 - 2 * x);
        // Evaluate polynomial
        return (edge0 * x) + (edge1 * (1 - x));
    }

    public static float smoothInterpolation(float bottomLeft, float topLeft, float bottomRight,
                              float topRight, float xMin, float xMax, float zMin,
                              float zMax, float x, float z)
    {
        float width = xMax - xMin, height = zMax - zMin;
        float xValue = 1 - (x - xMin) / width;
        float zValue = 1 - (z - zMin) / height;

        float a = smoothstep(bottomLeft, bottomRight, xValue);
        float b = smoothstep(topLeft, topRight, xValue);
        return smoothstep(a, b, zValue);
    }

}
