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
public class PNGTexture implements Texture{
    /** The default filtering method */
    public static final int DEFAULT_FILTERING_METHOD = GL_NEAREST_MIPMAP_NEAREST;

    /** The texture ID */
    public final int ID;
    /** The texture width */
    public final int width;
    /** The texture height */
    public final int height;
    /** Path to texture */
    public final Path path;

    /**
     * Constructs the texture from the PNG file, applying the default filtering method
     *
     * @param path The path to the PNG file
     * @throws IOException on a problem with reading the file
     */
    public PNGTexture(Path path) throws IOException{
        this(path, DEFAULT_FILTERING_METHOD);
    }

    /**
     * Constructs the texture from the PNG file
     *
     * @param path The path to the PNG file
     * @param filterMethod The filtering method (GL constant)
     * @throws IOException on a problem with reading the file
     */
    public PNGTexture(Path path, int filterMethod) throws IOException{
        //Read the file into a buffer
        PNGDecoder decoder = new PNGDecoder(Files.newInputStream(path.toAbsolutePath(), StandardOpenOption.READ));
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
        decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
        buffer.flip();

        //Set the data members
        width = decoder.getWidth();
        height = decoder.getHeight();
        this.path = path;

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
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filterMethod);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filterMethod);

        //Unbind the texture
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    @Override
    public int getID() {
        return ID;
    }

    /**
     * Cleans up the texture from the GPU
     */
    public void cleanUp(){
        glDeleteTextures(ID);
    }
}
