package net.pilif0.open_desert.graphics.shapes;

import net.pilif0.open_desert.Launcher;
import net.pilif0.open_desert.graphics.vertices.TextureVertex;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * Represents a shape used in a sprite.
 * This is more or less just a rectangular TextureShape, but with specific behaviour that does not allow extending
 * TextureShape.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class SpriteShape extends AbstractShape{
    /** The indices for a rectangle */
    public static final int[] INDICES = {0, 1, 2, 0, 3, 2 };

    /** The rectangle width */
    public final int width;
    /** The rectangle height */
    public final int height;

    /**
     * Constructs the shape - a textured rectangle
     *
     * @param width The rectangle width
     * @param height The rectangle height
     * @param texWidth The width of the texture that will be used with this file (in pixels)
     * @param texHeight The height of the texture that will be used with this file (in pixels)
     */
    public SpriteShape(int width, int height, int texWidth, int texHeight){
        //Log any previous OpenGL error (to clear the flags)
        Launcher.getLog().logOpenGLError("OpenGL", "before shape creation");

        //Set the data members
        this.width = width;
        this.height = height;

        //Build the vertices
        float hWidth = ((float) width) / 2;
        float hHeight = ((float) height) / 2;
        float tWidth = ((float) width) / ((float) texWidth);
        float tHeight = ((float) height) / ((float) texHeight);
        TextureVertex[] vertices = new TextureVertex[4];
        vertices[0] = new TextureVertex(-hWidth, -hHeight, 0, 0);
        vertices[1] = new TextureVertex(hWidth, -hHeight, tWidth, 0);
        vertices[2] = new TextureVertex(hWidth, hHeight, tWidth, tHeight);
        vertices[3] = new TextureVertex(-hWidth, hHeight, 0, tHeight);

        //There are two triangles in a rectangle
        vertexCount = 6;

        //Prepare VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        //Vertices
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer verticesBuffer = stack.mallocFloat(16);
            verticesBuffer.put(vertices[0].toInterleaved());
            verticesBuffer.put(vertices[1].toInterleaved());
            verticesBuffer.put(vertices[2].toInterleaved());
            verticesBuffer.put(vertices[3].toInterleaved());
            verticesBuffer.flip();

            vboID = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * 4, 0);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * 4, 2 * 4);
        }

        //Indices
        try(MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer indicesBuffer = stack.mallocInt(6);
            indicesBuffer.put(INDICES).flip();

            idxVboID = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboID);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        }

        //Unbind
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        //Log OpenGL errors
        Launcher.getLog().logOpenGLError("SpriteShape", "when creating");
    }

    /**
     * Cleans up the shape
     */
    public void cleanUp(){
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vboID);
        glDeleteBuffers(idxVboID);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoID);
    }
}
