package net.pilif0.open_desert.entities;

import net.pilif0.open_desert.geometry.Transformation;
import net.pilif0.open_desert.graphics.PerpendicularCamera;
import net.pilif0.open_desert.graphics.ShaderProgram;
import net.pilif0.open_desert.graphics.Shape;

/**
 * Represents a renderable object in the world, i.e. transformed shape.
 * This implementation uses the basic shape and shader implementations, therefore the shape is white.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class BasicEntity extends Entity implements Renderable{
    /** The entity shape */
    protected final Shape shape;
    /** The shader program used to render this entity */
    protected ShaderProgram program = ShaderProgram.BASIC_SHADER;

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
    public void render(PerpendicularCamera camera){
        //Bind the shader
        program.bind();

        //Set the uniforms
        program.setUniform("projectionMatrix", camera.getMatrix());
        program.setUniform("worldMatrix", getTransformation().getMatrix());

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
    public Shape getShape(){
        return shape;
    }

    /**
     * Cleans up after the entity
     */
    public void cleanUp(){}
}
