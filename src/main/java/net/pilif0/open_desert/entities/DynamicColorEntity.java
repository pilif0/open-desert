package net.pilif0.open_desert.entities;

import net.pilif0.open_desert.graphics.ColorShape;
import net.pilif0.open_desert.graphics.ShaderProgram;
import net.pilif0.open_desert.graphics.PerpendicularCamera;
import net.pilif0.open_desert.util.Color;
import org.joml.Vector2fc;

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
     * Constructs the entity with no transformation and white colour
     *
     * @param shape The shape
     */
    public DynamicColorEntity(ColorShape shape){
        super(shape);
        color = new Color(0xff_ff_ff_ff);
        program = ShaderProgram.DYNAMIC_COLOR_SHADER;
    }

    /**
     * Constructs the entity from all its properties
     *
     * @param shape The shape
     * @param position The position
     * @param scale The scale
     * @param rotation The rotation
     * @param color The color
     */
    public DynamicColorEntity(ColorShape shape, Vector2fc position, Vector2fc scale, float rotation, Color color){
        super(shape, position, scale, rotation);
        this.color = color;
        program = ShaderProgram.DYNAMIC_COLOR_SHADER;
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
