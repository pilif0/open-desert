package net.pilif0.open_desert.graphics;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Represents a texture that is segmented into smaller rectangles (of equal size).
 * It assumes that the segments are tightly-packed with no borders.
 * The segments are numbered in sequence from top left to bottom right (row first) starting with 0.
 * Used to more easily texture sprites.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class TextureAtlas extends Texture{
    /** The segment size (in the OpenGL format - from 0 to 1) */
    public final Vector2fc segmentSize;
    /** The number of columns of segments in this atlas */
    public final int cols;
    /** The number of rows of segments in this atlas */
    public final int rows;
    /** The number of segments in this atlas */
    public final int segments;

    /**
     * Constructs the texture atlas from the PNG file
     *
     * @param path The path to the PNG file
     * @param segmentSize The size of the segment (in the OpenGL format - from 0 to 1)
     * @throws IOException on a problem with reading the file
     */
    public TextureAtlas(Path path, Vector2fc segmentSize) throws IOException {
        super(path);
        this.segmentSize = segmentSize;
        cols = (int) (1 / segmentSize.x());
        rows = (int) (1 / segmentSize.y());
        segments = cols * rows;
    }

    /**
     * Constructs the texture atlas from the PNG file
     *
     * @param path The path to the PNG file
     * @param segmentWidth The width of the segment (in the OpenGL format - from 0 to 1)
     * @param segmentHeight The height of the segment (in the OpenGL format - from 0 to 1)
     * @throws IOException on a problem with reading the file
     */
    public TextureAtlas(Path path, float segmentWidth, float segmentHeight) throws IOException{
        this(path, new Vector2f(segmentWidth, segmentHeight));
    }

    /**
     * Constructs the texture atlas from the PNG file
     *
     * @param path The path to the PNG file
     * @param segmentWidth The width of the segment (in pixels of the picture)
     * @param segmentHeight The height of the segment (in pixels of the picture)
     * @throws IOException on a problem with reading the file
     */
    public TextureAtlas(Path path, int segmentWidth, int segmentHeight) throws IOException{
        super(path);
        segmentSize = new Vector2f(((float) segmentWidth) / ((float) width), ((float) segmentHeight) / ((float) height));
        cols = width / segmentWidth;
        rows = height / segmentHeight;
        segments = cols * rows;
    }

    /**
     * Calculates the coordinate shift required to move from the 0th segment to the ith segment
     *
     * @param i The number of the target segment
     * @return The coordinate shift required
     */
    public Vector2fc getDeltaCoordinates(int i){
        int col = i % cols;
        int row = i / cols;
        Vector2f result = (new Vector2f()).set(segmentSize).mul(col, row);
        return result;
    }
}
