package net.pilif0.open_desert.graphics;

import net.pilif0.open_desert.geometry.Transformation;

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
     * @param parentTransformation The transformation inherited from the parent
     */
    void render(PerpendicularCamera camera, Transformation parentTransformation);

    /**
     * Renders the object through the camera
     *
     * @param camera The camera to use when rendering
     */
    default void render(PerpendicularCamera camera){
        render(camera, Transformation.IDENTITY);
    }
}
