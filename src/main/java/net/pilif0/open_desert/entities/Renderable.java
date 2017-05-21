package net.pilif0.open_desert.entities;

import net.pilif0.open_desert.graphics.PerpendicularCamera;

/**
 * An interface for all things that can be rendered through a camera
 *
 * @author Filip Smola
 * @version 1.0
 */
public interface Renderable {

    /**
     * Renders the object through the camera
     *
     * @param camera The camera to use when rendering
     */
    void render(PerpendicularCamera camera);
}
