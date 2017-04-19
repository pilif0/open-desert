package net.pilif0.open_desert;

import net.pilif0.open_desert.graphics.Mesh;
import net.pilif0.open_desert.graphics.ShaderProgram;
import net.pilif0.open_desert.graphics.PerpendicularCamera;
import net.pilif0.open_desert.util.Color;
import org.joml.Vector3fc;

/**
 * A subclass of Entity that uses the coloured shader, thus its colour can change
 *
 * @author Filip Smola
 * @version 1.0
 */
public class ColoredEntity extends Entity {
    /** The entity colour multiplier (is multiplied with the mesh colour in the shader) */
    private Color color;

    /**
     * Constructs the entity with no transformation and white colour multiplier (preserves mesh colours)
     *
     * @param mesh The mesh
     */
    public ColoredEntity(Mesh mesh){
        super(mesh);
        color = new Color(0xff_ff_ff_ff);
        program = ShaderProgram.COLORED_SHADER;
    }

    /**
     * Constructs the entity from all its properties
     *
     * @param mesh The mesh
     * @param position The position
     * @param scale The scale
     * @param rotation The rotation
     * @param color The color
     */
    public ColoredEntity(Mesh mesh, Vector3fc position, Vector3fc scale, Vector3fc rotation, Color color){
        super(mesh, position, scale, rotation);
        this.color = color;
        program = ShaderProgram.COLORED_SHADER;
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

        //Render the mesh
        getMesh().render();

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
