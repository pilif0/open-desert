package net.pilif0.open_desert.entities;

import net.pilif0.open_desert.geometry.Transformation;
import net.pilif0.open_desert.graphics.*;
import org.joml.Vector2fc;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * Represents the objects in the world, i.e. transformed shapes.
 * This implementation uses a texture.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class TextureEntity {
    /** The entity shape */
    protected final TextureShape shape;
    /** The entity transformation */
    protected final Transformation transformation;
    /** The shader program used to render this entity */
    protected ShaderProgram program = ShaderProgram.TEXTURE_SHADER;
    /** The entity texture */
    protected Texture texture;

    /**
     * Constructs the entity with no transformations
     *
     * @param shape The shape
     * @param texture The texture
     */
    public TextureEntity(TextureShape shape, Texture texture){
        this.shape = shape;
        this.transformation = new Transformation();
        this.texture = texture;
    }

    /**
     * Constructs the entity from all its properties
     *
     * @param shape The shape
     * @param texture The texture
     * @param position The position
     * @param scale The scale
     * @param rotation The rotation
     */
    public TextureEntity(TextureShape shape, Texture texture, Vector2fc position, Vector2fc scale, float rotation){
        this.shape = shape;
        this.transformation = new Transformation(position, scale, rotation);
        this.texture = texture;
    }

    /**
     * Renders the entity
     */
    public void render(PerpendicularCamera camera){
        //Bind the shader
        program.bind();

        //Set the uniforms
        program.setUniform("projectionMatrix", camera.getMatrix());
        program.setUniform("worldMatrix", getTransformation().getMatrix());
        program.setUniform("textureSampler", 0);

        //Bind the texture
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.ID);

        //Render the shape
        getShape().render();

        //Unbind the texture from the target
        glBindTexture(GL_TEXTURE_2D, 0);

        //Restore the shader
        program.unbind();
    }

    /**
     * Returns the entity shape
     *
     * @return The shape
     */
    public TextureShape getShape(){
        return shape;
    }

    /**
     * Returns the entity texture
     *
     * @return The texture
     */
    public Texture getTexture(){
        return texture;
    }

    /**
     * Returns the entity transformation
     *
     * @return The entity transformation
     */
    public Transformation getTransformation(){ return transformation; }

    /**
     * Cleans up after the entity
     */
    public void cleanUp(){}
}
