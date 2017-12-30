package net.pilif0.open_desert.graphics.render;

import net.pilif0.open_desert.ecs.GameObject;
import org.joml.Matrix4fc;

/**
 * Render game objects onto the screen using specialised components containing the needed information
 *
 * @author Filip Smola
 * @version 1.0
 */
public interface Renderer {

    /**
     * Render the game object through the projection matrix
     *
     * @param projectionMatrix Projection matrix to use
     * @param go Game object to render
     */
    void render(Matrix4fc projectionMatrix, GameObject go);

    /**
     * Clean up after the renderer
     */
    void cleanUp();
}
