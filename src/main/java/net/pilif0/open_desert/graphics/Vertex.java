package net.pilif0.open_desert.graphics;

import net.pilif0.open_desert.util.Color;
import org.joml.Vector3f;

import java.util.Arrays;

/**
 * Represents a single vertex in a mesh - a combination of position and colour
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Vertex {
    /** The position of the vertex */
    public final float[] position;
    /** The colour of the vertex */
    public final float[] color;

    /**
     * Constructs a vertex from its position and colour components
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @param z The z coordinate
     * @param r The red colour component
     * @param g The green colour component
     * @param b The blue colour component
     * @param a The alpha colour component
     */
    public Vertex(float x, float y, float z, float r, float g, float b, float a){
        position = new float[]{x, y, z};
        color = new float[]{r, g, b, a};
    }

    /**
     * Returns the vertex data as "xyzrgba" for use in VBO data
     *
     * @return The interleaved vertex data
     */
    public float[] toInterleaved(){
        return new float[]{position[0], position[1], position[2], color[0], color[1], color[2], color[3]};
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "position=" + Arrays.toString(position) +
                ", color=" + Arrays.toString(color) +
                '}';
    }

    /**
     * Builds vertices
     */
    public static class VertexBuilder{
        /** The current position */
        public final Vector3f position;
        /** The current colour */
        public final Color color;

        /**
         * Constructs the builder with a starting position and colour
         *
         * @param position The starting position
         * @param color The starting colour
         */
        public VertexBuilder(Vector3f position, Color color){
            this.position = position;
            this.color = color;
        }

        /**
         * Builds a vertex at the current position with the current color
         *
         * @return The resulting vertex
         */
        public Vertex build(){
            return new Vertex(
                    position.x, position.y, position.z,
                    color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()
            );
        }
    }
}
