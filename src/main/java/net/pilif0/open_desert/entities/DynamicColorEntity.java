package net.pilif0.open_desert.entities;

import net.pilif0.open_desert.geometry.Transformation;
import net.pilif0.open_desert.graphics.ColorShape;
import net.pilif0.open_desert.graphics.ShaderProgram;
import net.pilif0.open_desert.graphics.PerpendicularCamera;
import net.pilif0.open_desert.util.Color;

/**
 * A subclass of ColorEntity that uses the dynamic colour shader, thus its colour can change
 *
 * @author Filip Smola
 * @version 1.0
 */
public class DynamicColorEntity extends ColorEntity {
    /** The entity colour (overrides the shape colour in the shader) */
    private Color color;

    /**
     * Constructs the entity from a shape with no transformation (identity) and white colour
     *
     * @param shape The shape
     */
    public DynamicColorEntity(ColorShape shape){
        super(shape);
        color = new Color(0xff_ff_ff_ff);
        program = ShaderProgram.DYNAMIC_COLOR_SHADER;
    }

    /**
     * Constructs the entity from its shape, transformation (wrt parent), and colour
     *
     * @param shape The shape
     * @param t The transformation
     * @param color The color
     */
    public DynamicColorEntity(ColorShape shape, Transformation t, Color color){
        super(shape, t);
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
     * Returns the entity colour
     *
     * @return The entity colour
     */
    public Color getColor(){ return color; }
}
