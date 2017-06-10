package net.pilif0.open_desert.graphics.shapes;

import java.nio.file.Paths;

/**
 * This class holds commonly used shapes to make their reusing easier.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Shapes {
    /**
     * Textured square with side of 1 and with texture of 1/16 by 1/16.
     * Used when rendering text using a 16x16 texture atlas.
     */
    public static final TextureShape CHARACTER_SQUARE = TextureShape.parse(Paths.get("shapes/CharacterSquare.shape"));

    /**
     * Cleans up all the static shapes
     */
    public static void cleanUp(){
        CHARACTER_SQUARE.cleanUp();
    }
}
