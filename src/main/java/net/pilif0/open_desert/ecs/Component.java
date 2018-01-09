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
    void handle(GameObjectEvent e);

    /**
     * Handle attaching to a game object (after the fact)
     *
     * @param owner Game object the component is being attached to
     */
    void onAttach(GameObject owner);

    /**
     * Handle detaching from a game object (after the fact)
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

    /**
     * Convert the component to a YAML object with respect to a template (only overrides with respect to the template)
     *
     * @param t Template to consider
     * @return YAML object or {@code null} if the same record is in the template already
     */
    Object toYaml(Template t);
}
