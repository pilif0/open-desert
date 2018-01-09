package net.pilif0.open_desert.graphics;

import org.joml.*;

/**
 * Represents a camera with perpendicular projection
 *
 * @author Filip Smola
 * @version 1.0
 */
public class PerpendicularCamera implements Camera {
    /** The closer z-axis clipping plane */
    public static final float NEAR = -10.0f;
    /** The further z-axis clipping plane */
    public static final float FAR = 10.0f;

    /** The position of the camera (top left corner) */
    private Vector2f position;
    /** The dimensions of the camera (width, height) */
    private Vector2i dimensions;
    /** The projection matrix */
    private Matrix4f matrix;
    /** Flag that indicates whether the camera was transformed and the matrix should be regenerated */
    private boolean transformed;

    /**
     * Construct the camera from its position and dimensions
     *
     * @param position The position (top left corner)
     * @param dimensions The dimensions
     */
    public PerpendicularCamera(Vector2fc position, Vector2ic dimensions){
        this.position = new Vector2f(position);
        this.dimensions = new Vector2i(dimensions);
        this.matrix = new Matrix4f();
        this.transformed = true;
    }

    /**
     * Moves the camera position
     *
     * @param difference The position difference
     */
    public void move(Vector2fc difference){
        position.add(difference);
        transformed = true;
    }

    /**
     * Returns the position
     *
     * @return The position
     */
    public Vector2fc getPosition() {
        return position.toImmutable();
    }

    /**
     * Sets the position
     *
     * @param position The new value
     */
    public void setPosition(Vector2fc position) {
        this.position.set(position);
        transformed = true;
    }

    /**
     * Returns the dimensions
     *
     * @return The dimensions
     */
    public Vector2ic getDimensions() {
        return dimensions.toImmutable();
    }

    /**
     * Sets the dimensions
     *
     * @param dimensions The new value
     */
    public void setDimensions(Vector2ic dimensions) {
        this.dimensions.set(dimensions);
        transformed = true;
    }

    @Override
    public Matrix4fc getMatrix() {
        return matrix.toImmutable();
    }

    @Override
    public void update(){
        if(transformed){
            regenerateMatrix();
        }
    }

    @Override
    public Vector2fc toWorldSpace(Vector2fc s) {
        // Just add the camera position
        return s.add(getPosition(), new Vector2f()).toImmutable();
    }

    /**
     * Regenerates the matrix and resets the flag
     */
    private void regenerateMatrix(){
        //Check that regeneration is needed
        if(!transformed){
            return;
        }

        //Gather the clipping plane values
        float top = position.y;
        float bottom = position.y + dimensions.y;
        float left = position.x;
        float right = position.x + dimensions.x;

        //Regenerate the matrix
        matrix.setOrtho(left, right, bottom, top, NEAR, FAR);

        //Reset the flag
        transformed = false;
    }
}
