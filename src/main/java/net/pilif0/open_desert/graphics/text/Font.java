package net.pilif0.open_desert.graphics.text;

import net.pilif0.open_desert.graphics.TextureAtlas;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.io.IOException;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.GL_NEAREST;


/**
 * Represents a font, which is a texture atlas with 16x16 segments.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Font extends TextureAtlas{
    /** The segment size of 1/16 x 1/16 */
    public static final Vector2fc SEGMENT_SIZE = new Vector2f(0.0625f, 0.0625f);

    /**
     * Constructs the font from the PNG file.
     * Applies the "nearest" filtering method
     *
     * @param path The path to the PNG file
     * @throws IOException on a problem with reading the file
     */
    public Font(Path path) throws IOException{
        super(path, SEGMENT_SIZE, GL_NEAREST);
    }

}
