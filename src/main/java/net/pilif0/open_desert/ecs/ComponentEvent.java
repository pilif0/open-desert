package net.pilif0.open_desert.ecs;

/**
 * Interface for events to be distributed by the game object
 *
 * @author Filip Smola
 * @version 1.0
 */
public interface ComponentEvent {

    /**
     * Get the component where the event originated
     *
     * @return Origin component
     */
    Component getOrigin();
}
