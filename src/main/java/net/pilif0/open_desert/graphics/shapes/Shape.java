package net.pilif0.open_desert.graphics.shapes;

import net.pilif0.open_desert.Launcher;
import net.pilif0.open_desert.graphics.vertices.Vertex;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * Represents a general shape in two dimensions (just the positions).
 * Attributes: 0 - 2D position
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Shape extends AbstractShape{
    /**
     * Constructs the shape
     *
     * @param vertices The vertices to use
     * @param indices The indices to use
     */
    public Shape(Vertex[] vertices, int[] indices){
        //Log any previous OpenGL error (to clear the flags)
        Launcher.getLog().logOpenGLError("OpenGL", "before shape creation");

        //Count vertices
        vertexCount = indices.length;

        //Prepare VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        //Vertices
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer verticesBuffer = stack.mallocFloat(vertices.length * 2);
            for(int i = 0; i < vertices.length; i++){
                verticesBuffer.put(vertices[i].position);
            }
            verticesBuffer.flip();

            vboID = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * 4, 0);
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
        Launcher.getLog().logOpenGLError("Shape", "when creating");
    }

    /**
     * Cleans up the shape
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
