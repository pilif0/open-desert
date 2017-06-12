package net.pilif0.open_desert.entities;

import net.pilif0.open_desert.entities.animation.AnimationDirector;
import net.pilif0.open_desert.geometry.Transformation;
import net.pilif0.open_desert.graphics.Renderable;
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
    }

    @Override
    public void render(PerpendicularCamera camera, Transformation parentTransformation){
        //Retrieve the shader
        ShaderProgram program = ShaderProgram.DYNAMIC_COLOR_SHADER;

        //Bind the shader
        program.bind();

        //Set the uniforms
        program.setUniform("projectionMatrix", camera.getMatrix());
        program.setUniform("worldMatrix", getTransformation().getMatrix());
        program.setUniform("parentMatrix", parentTransformation.getMatrix());
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
     * Adds an animation director to the list
     *
     * @param director The director to add
     */
    public void addDirector(DynamicColorEntityDirector director){
        super.addDirector(director);
    }

    /**
     * Animation director for the dynamic color entity
     */
    public static abstract class DynamicColorEntityDirector implements AnimationDirector {
        /** The entity to be directed */
        protected final DynamicColorEntity entity;

        /**
         * Constructs the entity director, giving it a reference to the entity to be directed
         *
         * @param entity The entity to be directed
         */
        public DynamicColorEntityDirector(DynamicColorEntity entity){
            this.entity = entity;
        }
    }
}
