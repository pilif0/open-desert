package net.pilif0.open_desert.geometry.planar;

import com.sun.istack.internal.NotNull;
import net.pilif0.open_desert.geometry.Vector2f;

/**
 * Represents a square with origin (bottom left corner) and side
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Square extends Rectangle{

    /**
     * Constructs the square from its origin and side
     *
     * @param origin The origin
     * @param side The side
     */
    public Square(@NotNull Vector2f origin, float side){
        super(origin, side, side);
    }
}
