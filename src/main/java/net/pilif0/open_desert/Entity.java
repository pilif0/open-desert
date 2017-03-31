package net.pilif0.open_desert;

import net.pilif0.open_desert.geometry.Transformation;
import net.pilif0.open_desert.graphics.Mesh;
import org.joml.Vector3fc;

/**
 * Represents the objects in the world, i.e. transformed meshes.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Entity {
    /** The entity mesh */
    private final Mesh mesh;
    /** The entity transformation */
    private final Transformation transformation;

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
