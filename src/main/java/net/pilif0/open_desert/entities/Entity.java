package net.pilif0.open_desert.entities;

import net.pilif0.open_desert.entities.animation.AnimationDirector;
import net.pilif0.open_desert.geometry.Transformation;

import java.util.ArrayList;
import java.util.List;

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
    /** List of animation directors */
    protected final List<AnimationDirector> animationDirectors = new ArrayList<>();

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
     * Updates the entity, updating all the animation directors
     */
    public void update(){
        animationDirectors.forEach(d -> d.update());
    }

    /**
     * Adds an animation director to the list
     *
     * @param director The director to add
     */
    public void addDirector(AnimationDirector director){
        animationDirectors.add(director);
    }

    /**
     * Returns the list of animation directors
     *
     * @return The list of animation directors
     */
    public List<AnimationDirector> getAnimationDirectors() {
        return animationDirectors;
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
