package net.pilif0.open_desert.graphics.vertices;

import org.joml.Vector2f;

import java.util.Arrays;

/**
 * Represents a single textured vertex in a shape - a combination of position and texture coordinates
 *
 * @author Filip Smola
 * @version 1.0
 */
public class TextureVertex extends Vertex{
    /** The texture coordinates of the vertex */
    public final float[] texCoords;

    /**
     * Constructs a vertex from its position and colour components
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @param tX The red colour component
     * @param tY The green colour component
     */
    public TextureVertex(float x, float y, float tX, float tY){
        super(x, y);
        texCoords = new float[]{tX, tY};
    }

    /**
     * Returns the vertex data as "xytXtY" for use in VBO data
     *
     * @return The interleaved vertex data
     */
    @Override
    public float[] toInterleaved(){
        return new float[]{position[0], position[1], texCoords[0], texCoords[1]};
    }

    @Override
    public String toString() {
        return "ColorVertex{" +
                "position=" + Arrays.toString(position) +
                ", texCoords=" + Arrays.toString(texCoords) +
                '}';
    }

    /**
     * Builds vertices
     */
    public static class TexturedVertexBuilder{
        /** The current position */
        public final Vector2f position;
        /** The current colour */
        public final Vector2f texCoords;

        /**
         * Constructs the builder with a starting position and colour
         *
         * @param position The starting position
         * @param texCoords The starting texture coordinate
         */
        public TexturedVertexBuilder(Vector2f position, Vector2f texCoords){
            this.position = position;
            this.texCoords = texCoords;
        }

        /**
         * Builds a vertex at the current position with the current texture coordinates
         *
         * @return The resulting vertex
         */
        public TextureVertex build(){
            return new TextureVertex(
                    position.x, position.y,
                    texCoords.x, texCoords.y
            );
        }
    }
}
