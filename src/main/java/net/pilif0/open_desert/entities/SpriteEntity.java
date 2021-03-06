package net.pilif0.open_desert.entities;

import net.pilif0.open_desert.entities.animation.AnimationDirector;
import net.pilif0.open_desert.geometry.Transformation;
import net.pilif0.open_desert.graphics.*;
import net.pilif0.open_desert.graphics.shapes.SpriteShape;
import org.joml.Vector2fc;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * Represents the objects in the world, i.e. transformed shapes.
 * This implementation uses a texture atlas.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class SpriteEntity extends Entity implements Renderable {
    /** The entity shape */
    protected final SpriteShape shape;
    /** The entity texture atlas */
    protected TextureAtlas textureAtlas;
    /** The number of the currently used segment from the texture atlas */
    protected int segment;
    /** The current coordinate shift required to move to the current segment on the texture */
    protected Vector2fc textureDelta;

    /**
     * Constructs the entity from a shape and texture atlas, with no transformation (identity)
     *
     * @param shape The shape
     * @param textureAtlas The texture atlas
     */
    public SpriteEntity(SpriteShape shape, TextureAtlas textureAtlas){
        super();
        this.shape = shape;
        this.textureAtlas = textureAtlas;
        segment = 0;
        textureDelta = textureAtlas.getDeltaCoordinates(0);
    }

    /**
     * Constructs the entity from its shape, texture atlas, and transformation (wrt parent)
     *
     * @param shape The shape
     * @param textureAtlas The texture atlas
     * @param t The transformation
     */
    public SpriteEntity(SpriteShape shape, TextureAtlas textureAtlas, Transformation t, int segment){
        super(t);
        this.shape = shape;
        this.textureAtlas = textureAtlas;
        this.segment = segment;
        textureDelta = textureAtlas.getDeltaCoordinates(segment);
    }

    @Override
    public void render(PerpendicularCamera camera, Transformation parentTransformation){
        //Retrieve the shader
        ShaderProgram program = Shaders.get(ShaderProgram.SPRITE_SHADER);

        //Bind the shader
        program.bind();

        //Set the uniforms

        program.setUniform("projectionMatrix", camera.getMatrix());
        program.setUniform("worldMatrix", getTransformation().getMatrix());
        program.setUniform("parentMatrix", parentTransformation.getMatrix());
        program.setUniform("textureSampler", 0);
        program.setUniform("textureDelta", textureDelta);

        //Bind the texture
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureAtlas.ID);

        //Render the shape
        getShape().render();

        //Unbind the texture from the target
        glBindTexture(GL_TEXTURE_2D, 0);

        //Restore the shader
        ShaderProgram.unbind();
    }

    /**
     * Returns the entity shape
     *
     * @return The shape
     */
    public SpriteShape getShape(){
        return shape;
    }

    /**
     * Returns the entity texture atlas
     *
     * @return The texture atlas
     */
    public TextureAtlas getTextureAtlas(){
        return textureAtlas;
    }

    /**
     * Returns the current segment from the texture atlas
     *
     * @return The current segment
     */
    public int getSegment() {
        return segment;
    }

    /**
     * Sets the segment to use from the texture atlas
     *
     * @param segment The new value
     */
    public void setSegment(int segment) {
        this.segment = segment;
        this.textureDelta = textureAtlas.getDeltaCoordinates(segment);
    }

    /**
     * Adds an animation director to the list
     *
     * @param director The director to add
     */
    public void addDirector(SpriteEntityDirector director){
        super.addDirector(director);
    }

    /**
     * Animation director for the sprite entity
     */
    public static abstract class SpriteEntityDirector implements AnimationDirector {
        /** The entity to be directed */
        protected final SpriteEntity entity;

        /**
         * Constructs the entity director, giving it a reference to the entity to be directed
         *
         * @param entity The entity to be directed
         */
        public SpriteEntityDirector(SpriteEntity entity){
            this.entity = entity;
        }
    }
}
