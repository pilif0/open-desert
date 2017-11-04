package net.pilif0.open_desert.components;

import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * Component that only contains position and related operations.
 * This is the most basic component guaranteed to be present in all game objects.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class PositionComponent {
    //TODO

    /** Position value */
    private Vector2f position;

    /**
     * Get the position value
     *
     * @return Position value
     */
    public Vector2fc getPosition(){
        return position;
    }
}
