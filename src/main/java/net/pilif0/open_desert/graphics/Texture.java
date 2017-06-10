package net.pilif0.open_desert.graphics;

/**
 * Interface for textures of all kinds
 *
 * @author Filip Smola
 * @version 1.0
 */
public interface Texture {
    /**
     * Returns the texture ID
     *
     * @return The texture ID
     */
    int getID();

    /**
     * Cleans up the texture
     */
    void cleanUp();
}
