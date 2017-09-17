package net.pilif0.open_desert.entities;

import net.pilif0.open_desert.entities.animation.AnimationDirector;
import net.pilif0.open_desert.geometry.Transformation;
import net.pilif0.open_desert.graphics.PerpendicularCamera;
import net.pilif0.open_desert.graphics.Renderable;
import net.pilif0.open_desert.graphics.ShaderProgram;
import net.pilif0.open_desert.graphics.Shaders;
import net.pilif0.open_desert.graphics.shapes.ColorShape;

/**
 * Represents the objects in the world, i.e. transformed shapes.
 * This implementation has a static colour that is given by the ColorShape.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class ColorEntity extends Entity implements Renderable {
    /** The entity shape */
    protected final ColorShape shape;

    /**
     * Constructs the entity from a shape with no transformation (identity)
     *
     * @param shape The shape
     */
    public ColorEntity(ColorShape shape){
        super();
        this.shape = shape;
    }

    /**
     * Constructs the entity from its shape and transformation (wrt parent)
     *
     * @param shape The shape
     * @param t The transformation
     */
    public ColorEntity(ColorShape shape, Transformation t){
        super(t);
        this.shape = shape;
    }

    @Override
    public void render(PerpendicularCamera camera, Transformation parentTransformation){
        //Retrieve the shader
        ShaderProgram program = Shaders.get(ShaderProgram.STATIC_COLOR_SHADER);

        //Bind the shader
        program.bind();

        //Set the uniforms
        program.setUniform("projectionMatrix", camera.getMatrix());
        program.setUniform("worldMatrix", getTransformation().getMatrix());
        program.setUniform("parentMatrix", parentTransformation.getMatrix());

        //Render the shape
        getShape().render();

        //Restore the shader
        program.unbind();
    }

    /**
     * Returns the entity shape
     *
     * @return The shape
     */
    public ColorShape getShape(){
        return shape;
    }

    /**
     * Adds an animation director to the list
     *
     * @param director The director to add
     */
    public void addDirector(ColorEntityDirector director){
        super.addDirector(director);
    }

    /**
     * Animation director for the color entity
     */
    public static abstract class ColorEntityDirector implements AnimationDirector {
        /** The entity to be directed */
        protected final ColorEntity entity;

        /**
         * Constructs the entity director, giving it a reference to the entity to be directed
         *
         * @param entity The entity to be directed
         */
        public ColorEntityDirector(ColorEntity entity){
            this.entity = entity;
        }
    }
}
