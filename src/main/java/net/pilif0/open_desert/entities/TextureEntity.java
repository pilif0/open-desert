package net.pilif0.open_desert.entities;

import net.pilif0.open_desert.entities.animation.AnimationDirector;
import net.pilif0.open_desert.geometry.Transformation;
import net.pilif0.open_desert.graphics.*;
import net.pilif0.open_desert.graphics.shapes.TextureShape;

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
public class TextureEntity extends Entity implements Renderable {
    /** The entity shape */
    protected final TextureShape shape;
    /** The entity texture */
    protected Texture texture;

    /**
     * Constructs the entity from a shape and texture, with no transformation (identity)
     *
     * @param shape The shape
     * @param texture The texture
     */
    public TextureEntity(TextureShape shape, Texture texture){
        super();
        this.shape = shape;
        this.texture = texture;
    }

    /**
     * Constructs the entity from its shape, texture, and transformation (wrt parent)
     *
     * @param shape The shape
     * @param texture The texture
     * @param t The transformation
     */
    public TextureEntity(TextureShape shape, Texture texture, Transformation t){
        super(t);
        this.shape = shape;
        this.texture = texture;
    }

    @Override
    public void render(PerpendicularCamera camera, Transformation parentTransformation){
        //Retrieve the shader
        ShaderProgram program = Shaders.get(ShaderProgram.TEXTURE_SHADER);

        //Bind the shader
        program.bind();

        //Set the uniforms
        program.setUniform("projectionMatrix", camera.getMatrix());
        program.setUniform("worldMatrix", getTransformation().getMatrix());
        program.setUniform("parentMatrix", parentTransformation.getMatrix());
        program.setUniform("textureSampler", 0);

        //Bind the texture
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.getID());

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
     * Adds an animation director to the list
     *
     * @param director The director to add
     */
    public void addDirector(TextureEntityDirector director){
        super.addDirector(director);
    }

    /**
     * Animation director for the texture entity
     */
    public static abstract class TextureEntityDirector implements AnimationDirector {
        /** The entity to be directed */
        protected final TextureEntity entity;

        /**
         * Constructs the entity director, giving it a reference to the entity to be directed
         *
         * @param entity The entity to be directed
         */
        public TextureEntityDirector(TextureEntity entity){
            this.entity = entity;
        }
    }
}
