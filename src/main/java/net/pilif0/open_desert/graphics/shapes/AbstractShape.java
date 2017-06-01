package net.pilif0.open_desert.graphics.shapes;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * Represents a template for all different shape types
 *
 * @author Filip Smola
 * @version 1.0
 */
public abstract class AbstractShape {
    /** ID of the vertex array object */
    protected int vaoID;
    /** ID of the vertex buffer object */
    protected int vboID;
    /** ID of the index VBO */
    protected int idxVboID;
    /** Number of VERTICES */
    protected int vertexCount;

    /**
     * Renders the shape
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
     * Cleans up the shape
     */
    public abstract void cleanUp();

    /**
     * Returns the VAO ID
     *
     * @return The VAO ID
     */
    public int getVaoID() {
        return vaoID;
    }

    /**
     * Returns the VBO ID
     *
     * @return The VBO ID
     */
    public int getVboID() {
        return vboID;
    }

    /**
     * Returns the index VBO ID
     *
     * @return The index VBO ID
     */
    public int getIdxVboID() {
        return idxVboID;
    }

    /**
     * Returns the vertex count
     *
     * @return The vertex count
     */
    public int getVertexCount() {
        return vertexCount;
    }
}
