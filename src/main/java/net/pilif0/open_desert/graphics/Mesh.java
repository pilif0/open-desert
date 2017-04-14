package net.pilif0.open_desert.graphics;

import net.pilif0.open_desert.Launcher;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Represents a general mesh in three dimensions
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Mesh {
    /** ID of the vertex array object */
    public final int vaoID;
    /** ID of the vertex buffer object */
    private final int vboID;
    /** ID of the index VBO */
    private final int idxVboID;
    /** Number of VERTICES */
    public final int vertexCount;

    /**
     * Constructs the mesh
     *
     * @param vertices The vertices to use
     * @param indices The indices to use
     */
    public Mesh(Vertex[] vertices, int[] indices){
        //Log any previous OpenGL error (to clear the flags)
        Launcher.getLog().logOpenGLError("OpenGL", "before mesh creation");

        //Count vertices
        vertexCount = indices.length;

        //Prepare VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        //Vertices
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer verticesBuffer = stack.mallocFloat(vertices.length * 7);
            for(int i = 0; i < vertices.length; i++){
                verticesBuffer.put(vertices[i].toInterleaved());
            }
            verticesBuffer.flip();

            vboID = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 7 * 4, 0);
            glVertexAttribPointer(1, 4, GL_FLOAT, false, 7 * 4, 3 * 4);
        }

        //Indices
        try(MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer indicesBuffer = stack.mallocInt(indices.length);
            indicesBuffer.put(indices).flip();

            idxVboID = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboID);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        }

        //Unbind
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        //Log OpenGL errors
        Launcher.getLog().logOpenGLError("Mesh", "when creating");
    }

    /**
     * Renders the mesh
     */
    public void render(){
        //Bind the VAO
        glBindVertexArray(vaoID);

        //Draw
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

        //Restore
        glBindVertexArray(0);
    }

    /**
     * Cleans up the mesh
     */
    public void cleanUp(){
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vboID);
        glDeleteBuffers(idxVboID);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoID);
    }

}
