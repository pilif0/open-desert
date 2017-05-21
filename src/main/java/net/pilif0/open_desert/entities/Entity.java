package net.pilif0.open_desert.entities;

import net.pilif0.open_desert.geometry.Transformation;
import net.pilif0.open_desert.graphics.PerpendicularCamera;
import net.pilif0.open_desert.graphics.ShaderProgram;
import net.pilif0.open_desert.graphics.Shape;
import org.joml.Vector2fc;

/**
 * Represents the objects in the world, i.e. transformed shapes.
 * This implementation uses the basic shape and mesh implementations, therefore the shape is white.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Entity implements Renderable{
    /** The entity shape */
    protected final Shape shape;
    /** The entity transformation */
    protected final Transformation transformation;
    /** The shader program used to render this entity */
    protected ShaderProgram program = ShaderProgram.BASIC_SHADER;

    /**
     * Constructs the entity with no transformations
     *
     * @param shape The shape
     */
    public Entity(Shape shape){
        this.shape = shape;
        this.transformation = new Transformation();
    }

    /**
     * Constructs the entity from all its properties
     *
     * @param shape The shape
     * @param position The position
     * @param scale The scale
     * @param rotation The rotation
     */
    public Entity(Shape shape, Vector2fc position, Vector2fc scale, float rotation){
        this.shape = shape;
        this.transformation = new Transformation(position, scale, rotation);
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
