package net.pilif0.open_desert.entities;

import net.pilif0.open_desert.geometry.Transformation;

/**
 * Represents a simple entity in the world.
 * This implementation is not renderable, it just has a transformation (position, rotation, scale) wrt parent.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Entity {
    /** The entity transformation */
    protected final Transformation transformation;

    /**
     * Constructs an entity with identity transformation
     */
    public Entity(){
        transformation = new Transformation();
    }

    /**
     * Constructs an entity with an initial transformation
     *
     * @param t The initial transformation (is copied)
     */
    public Entity(Transformation t){
        transformation = new Transformation(t);
    }

    /**
     * Returns the entity transformation (wrt parent)
     *
     * @return The entity transformation
     */
    public Transformation getTransformation(){ return transformation; }

    /**
     * Cleans up after the entity
     */
    public void cleanUp(){}
}
