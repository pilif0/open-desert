package net.pilif0.open_desert.graphics;

import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

/**
 * Represents a 2D texture (from a PNG in RGBA format)
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Texture {
    /** The texture ID */
    public final int ID;
    /** The texture width */
    public final int width;
    /** The texture height */
    public final int height;

    /**
     * Constructs the texture from the PNG file
     *
     * @param path The path to the PNG file
     * @throws IOException on a problem with reading the file
     */
    public Texture(Path path) throws IOException{
        //Read the file into a buffer
        PNGDecoder decoder = new PNGDecoder(Files.newInputStream(path, StandardOpenOption.READ));
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
        decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
        buffer.flip();

        //Set the data members
        width = decoder.getWidth();
        height = decoder.getHeight();

        //Upload the texture to the GPU
        ID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, ID);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RGBA,
                decoder.getWidth(),
                decoder.getHeight(),
                0,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                buffer
        );
        glGenerateMipmap(GL_TEXTURE_2D);

        //Unbind the texture
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Cleans up the texture from the GPU
     */
    public void cleanUp(){
        glDeleteTextures(ID);
    }
}
