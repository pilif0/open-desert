package net.pilif0.open_desert.entities;

import net.pilif0.open_desert.geometry.Transformation;
import net.pilif0.open_desert.graphics.ShaderProgram;
import net.pilif0.open_desert.graphics.PerpendicularCamera;
import net.pilif0.open_desert.graphics.shapes.Shape;
import net.pilif0.open_desert.util.Color;

/**
 * Represents the objects in the world, i.e. transformed shapes.
 * This implementation uses a dynamic colour.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class DynamicColorEntity extends Entity implements Renderable {
    /** The entity colour */
    private Color color;
    /** The entity shape */
    protected final Shape shape;
    /** The shader program used to render this entity */
    protected ShaderProgram program = ShaderProgram.DYNAMIC_COLOR_SHADER;

    /**
     * Constructs the entity from a shape with no transformation (identity) and white colour
     *
     * @param shape The shape
     */
    public DynamicColorEntity(Shape shape){
        this.shape = shape;
        color = new Color(0xff_ff_ff_ff);
    }

    /**
     * Constructs the entity from its shape, transformation (wrt parent), and colour
     *
     * @param shape The shape
     * @param t The transformation
     * @param color The color
     */
    public DynamicColorEntity(Shape shape, Transformation t, Color color){
        super(t);
        this.shape = shape;
        this.color = color;
        program = ShaderProgram.DYNAMIC_COLOR_SHADER;
    }

    @Override
    public void render(PerpendicularCamera camera){
        //Bind the shader
        program.bind();

        //Set the uniforms
        program.setUniform("projectionMatrix", camera.getMatrix());
        program.setUniform("worldMatrix", getTransformation().getMatrix());
        program.setUniform("color", getColor().toVector());

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
     * Returns the entity colour
     *
     * @return The entity colour
     */
    public Color getColor(){ return color; }

    /**
     * Cleans up after the entity
     */
    public void cleanUp(){}
}
