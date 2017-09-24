package net.pilif0.open_desert.entities;

import net.pilif0.open_desert.entities.animation.AnimationDirector;
import net.pilif0.open_desert.geometry.Transformation;
import net.pilif0.open_desert.graphics.PerpendicularCamera;
import net.pilif0.open_desert.graphics.Renderable;
import net.pilif0.open_desert.graphics.ShaderProgram;
import net.pilif0.open_desert.graphics.Shaders;
import net.pilif0.open_desert.graphics.shapes.Shape;

/**
 * Represents a renderable object in the world, i.e. transformed shape.
 * This implementation uses the basic shape and shader implementations, therefore the shape is white.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class BasicEntity extends Entity implements Renderable {
    /** The entity shape */
    protected final Shape shape;

    /**
     * Constructs the entity from a shape with no transformation (identity)
     *
     * @param shape The shape
     */
    public BasicEntity(Shape shape){
        super();
        this.shape = shape;
    }

    /**
     * Constructs the entity from its shape and transformation (wrt parent)
     *
     * @param shape The shape
     * @param t The transformation
     */
    public BasicEntity(Shape shape, Transformation t){
        super(t);
        this.shape = shape;
    }

    @Override
    public void render(PerpendicularCamera camera, Transformation parentTransformation){
        //Retrieve the shader
        ShaderProgram program = Shaders.get(ShaderProgram.BASIC_SHADER);

        //Bind the shader
        program.bind();

        //Set the uniforms
        program.setUniform("projectionMatrix", camera.getMatrix());
        program.setUniform("worldMatrix", getTransformation().getMatrix());
        program.setUniform("parentMatrix", parentTransformation.getMatrix());

        //Render the shape
        getShape().render();

        //Restore the shader
        ShaderProgram.unbind();
    }

    /**
     * Returns the entity shape
     *
     * @return The shape
     */
    public Shape getShape(){
        return shape;
    }

    /**
     * Adds an animation director to the list
     *
     * @param director The director to add
     */
    public void addDirector(BasicEntityDirector director){
        super.addDirector(director);
    }

    /**
     * Animation director for the basic entity
     */
    public static abstract class BasicEntityDirector implements AnimationDirector {
        /** The entity to be directed */
        protected final BasicEntity entity;

        /**
         * Constructs the entity director, giving it a reference to the entity to be directed
         *
         * @param entity The entity to be directed
         */
        public BasicEntityDirector(BasicEntity entity){
            this.entity = entity;
        }
    }
}
