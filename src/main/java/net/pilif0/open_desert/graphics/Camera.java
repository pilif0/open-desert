package net.pilif0.open_desert.graphics;

import org.joml.Matrix4f;

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
    Matrix4f getMatrix();

    /**
     * Update the camera
     */
    void update();
}
