package net.pilif0.open_desert.geometry.planar;

import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * Represents a rectangle with origin (bottom left corner) and dimensions.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Rectangle {
    /** An enumeration of the four corners */
    public enum Corner {TOP_LEFT, TOP_RIGHT, BOT_LEFT, BOT_RIGHT}

    /** The origin of the rectangle (the bottom left corner) */
    private final Vector2f origin;
    /** The dimensions of the rectangle (vector from bottom left to top right) */
    private final Vector2f dimensions;

    /**
     * Constructs the rectangle from its origin and dimensions
     *
     * @param origin The origin
     * @param dimensions The dimensions
     */
    public Rectangle(Vector2f origin, Vector2f dimensions){
        this.origin = origin;
        this.dimensions = dimensions;
    }

    /**
     * Constructs the rectangle from its origin and dimensions
     *
     * @param origin The origin
     * @param width The width
     * @param height The height
     */
    public Rectangle(Vector2f origin, float width, float height){
        this(origin, new Vector2f(width, height));
    }

    /**
     * Returns the position vector of the centre of this rectangle
     *
     * @return THe position vector of the centre of this rectangle
     */
    public Vector2f getCentre(){
        return new Vector2f(origin.x + (dimensions.x /2), origin.y + (dimensions.y /2));
    }

    /**
     * Returns the width of this rectangle
     *
     * @return The width of this rectangle
     */
    public float getWidth(){ return dimensions.x; }

    /**
     * Returns the height of this rectangle
     *
     * @return The height of this rectangle
     */
    public float getHeight(){ return dimensions.y; }

    /**
     * Returns the position vector of the desired corner of the rectangle
     *
     * @param c The desired corner
     * @return The position vector of the corner
     */
    public Vector2fc getCorner(Corner c){
        switch(c){
            case BOT_LEFT: return origin.toImmutable();
            case BOT_RIGHT: return origin.add(dimensions.x, 0, new Vector2f()).toImmutable();
            case TOP_LEFT: return origin.add(0, dimensions.y, new Vector2f()).toImmutable();
            case TOP_RIGHT: return origin.add(dimensions, new Vector2f()).toImmutable();
            default: return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rectangle rectangle = (Rectangle) o;

        if (!origin.equals(rectangle.origin)) return false;
        return dimensions.equals(rectangle.dimensions);
    }

    @Override
    public int hashCode() {
        int result = origin.hashCode();
        result = 31 * result + dimensions.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("Rectangle {origin: %s, dimensions: %s}", origin, dimensions);
    }
}
