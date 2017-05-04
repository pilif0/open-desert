package net.pilif0.open_desert.entities;

import net.pilif0.open_desert.geometry.Transformation;
import net.pilif0.open_desert.graphics.ColorShape;
import net.pilif0.open_desert.graphics.ShaderProgram;
import net.pilif0.open_desert.graphics.PerpendicularCamera;
import org.joml.Vector2fc;

/**
 * Represents the objects in the world, i.e. transformed shapes.
 * This implementation has a static colour that is given by the ColorShape.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class ColorEntity {
    /** The entity shape */
    protected final ColorShape shape;
    /** The entity transformation */
    protected final Transformation transformation;
    /** The shader program used to render this entity */
    protected ShaderProgram program = ShaderProgram.STATIC_COLOR_SHADER;

    /**
     * Constructs the entity with no transformations
     *
     * @param shape The shape
     */
    public ColorEntity(ColorShape shape){
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
    public ColorEntity(ColorShape shape, Vector2fc position, Vector2fc scale, float rotation){
        this.shape = shape;
        this.transformation = new Transformation(position, scale, rotation);
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
