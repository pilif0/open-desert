package net.pilif0.open_desert.graphics;

import org.joml.Matrix4fc;
import org.joml.Vector2fc;

/**
 * General camera interface
 *
 * @author Filip Smola
 * @version 1.0
 */
public interface Camera {

    /**
     * Return the projection matrix
     *
     * @return Projection matrix
     */
    Matrix4fc getMatrix();

    /**
     * Update the camera
     */
    void update();

    /**
     * Return the vector transformed to world space
     *
     * @param s Vector in screen space
     * @return Vector in world space
     */
    Vector2fc toWorldSpace(Vector2fc s);
}
