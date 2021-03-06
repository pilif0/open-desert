package net.pilif0.open_desert.graphics.vertices;

import net.pilif0.open_desert.util.Color;
import org.joml.Vector2f;

import java.util.Arrays;

/**
 * Represents a single coloured vertex in a shape - a combination of position and colour
 *
 * @author Filip Smola
 * @version 1.0
 */
public class ColorVertex extends Vertex{
    /** The colour of the vertex */
    public final float[] color;

    /**
     * Constructs a vertex from its position and colour components
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @param r The red colour component
     * @param g The green colour component
     * @param b The blue colour component
     * @param a The alpha colour component
     */
    public ColorVertex(float x, float y, float r, float g, float b, float a){
        super(x, y);
        color = new float[]{r, g, b, a};
    }

    /**
     * Returns the vertex data as "xyrgba" for use in VBO data
     *
     * @return The interleaved vertex data
     */
    @Override
    public float[] toInterleaved(){
        return new float[]{position[0], position[1], color[0], color[1], color[2], color[3]};
    }

    @Override
    public String toString() {
        return "ColorVertex{" +
                "position=" + Arrays.toString(position) +
                ", color=" + Arrays.toString(color) +
                '}';
    }

    /**
     * Builds vertices
     */
    public static class ColouredVertexBuilder {
        /** The current position */
        public final Vector2f position;
        /** The current colour */
        public final Color color;

        /**
         * Constructs the builder with a starting position and colour
         *
         * @param position The starting position
         * @param color The starting colour
         */
        public ColouredVertexBuilder(Vector2f position, Color color){
            this.position = position;
            this.color = color;
        }

        /**
         * Builds a vertex at the current position with the current color
         *
         * @return The resulting vertex
         */
        public ColorVertex build(){
            return new ColorVertex(
                    position.x, position.y,
                    color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()
            );
        }
    }
}
