package net.pilif0.open_desert.ecs;

import java.util.Map;

/**
 * Interface for components
 *
 * @author Filip Smola
 * @version 1.0
 */
public interface Component {
    /**
     * Get the component name (unique to the component type in the system)
     *
     * @return Component name
     */
    String getName();

    /**
     * Handles events distributed by the game object
     *
     * @param e Event to handle
     */
    void handle(ComponentEvent e);

    /**
     * Get the component state as a map of field names to values (as Objects with meaningful {@code toString()}).
     * This summarises the component state in an easy-to-read way.
     *
     * @return Component state map
     */
    // Mainly to be used in debugging
    Map<String, Object> getState();
}
