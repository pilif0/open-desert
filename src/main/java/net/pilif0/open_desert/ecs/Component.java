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

    /**
     * Handle attaching to a game object
     *
     * @param owner Game object the component is being attached to
     */
    void onAttach(GameObject owner);

    /**
     * Handle detaching from a game object
     *
     * @param owner Game object the component is being detached from
     */
    void onDetach(GameObject owner);

    /**
     * Override field values with the provided ones
     *
     * @param overrides New fields values
     */
    // For instantiating from templates and serialised game objects
    void overrideFields(Map<String, Object> overrides);
}
