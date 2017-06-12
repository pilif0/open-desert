package net.pilif0.open_desert.entities.animation;

/**
 * Provides a way of updating an entity without hard-coding it explicitly into the entity class.
 * Also allows easier reusing of behaviour.
 *
 * @author Filip Smola
 * @version 1.0
 */
public interface AnimationDirector {

    /**
     * Updates the animation director
     */
    void update();
}
