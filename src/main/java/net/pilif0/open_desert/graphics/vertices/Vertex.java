package net.pilif0.open_desert.graphics.vertices;

import org.joml.Vector2f;

import java.util.Arrays;

/**
 * Represents a single vertex in a shape
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Vertex {
    /** The position of the vertex */
    public final float[] position;

    /**
     * Constructs a vertex from its position
     *
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public Vertex(float x, float y){
        position = new float[]{x, y};
    }

    /**
     * Returns the vertex data as "xy" for use in VBO data
     *
     * @return The interleaved vertex data
     */
    public float[] toInterleaved(){
        return new float[]{position[0], position[1]};
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "position=" + Arrays.toString(position) +
                '}';
    }

    /**
     * Builds vertices
     */
    public static class VertexBuilder {
        /** The current position */
        public final Vector2f position;

        /**
         * Constructs the builder with a starting position
         *
         * @param position The starting position
         */
        public VertexBuilder(Vector2f position){
            this.position = position;
        }

        /**
         * Builds a vertex at the current position with the current color
         *
         * @return The resulting vertex
         */
        public Vertex build(){
            return new Vertex(
                    position.x, position.y
            );
        }
    }
}
