package net.pilif0.open_desert;

import net.pilif0.open_desert.geometry.Transformation;
import net.pilif0.open_desert.graphics.Mesh;
import net.pilif0.open_desert.graphics.ShaderProgram;
import net.pilif0.open_desert.graphics.TopDownCamera;
import org.joml.Vector3fc;

/**
 * Represents the objects in the world, i.e. transformed meshes.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Entity {
    /** The entity mesh */
    protected final Mesh mesh;
    /** The entity transformation */
    protected final Transformation transformation;
    /** The shader program used to render this entity */
    protected ShaderProgram program = ShaderProgram.BASIC_SHADER;

    /**
     * Constructs the entity with no transformations
     *
     * @param mesh The mesh
     */
    public Entity(Mesh mesh){
        this.mesh = mesh;
        this.transformation = new Transformation();
    }

    /**
     * Constructs the entity from all its properties
     *
     * @param mesh The mesh
     * @param position The position
     * @param scale The scale
     * @param rotation The rotation
     */
    public Entity(Mesh mesh, Vector3fc position, Vector3fc scale, Vector3fc rotation){
        this.mesh = mesh;
        this.transformation = new Transformation(position, scale, rotation);
    }

    /**
     * Renders the entity
     */
    public void render(TopDownCamera camera){
        //Bind the shader
        program.bind();

        //Set the uniforms
        program.setUniform("projectionMatrix", camera.getMatrix());
        program.setUniform("worldMatrix", getTransformation().getMatrix());

        //Render the mesh
        getMesh().render();

        //Restore the shader
        program.unbind();
    }

    /**
     * Returns the entity mesh
     *
     * @return The mesh
     */
    public Mesh getMesh(){
        return mesh;
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
    public void cleanUp(){ mesh.cleanUp();}
}
