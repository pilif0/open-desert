package net.pilif0.open_desert.geometry;

import com.sun.istack.internal.NotNull;

/**
 * Represents a vector in two dimensions
 *
 * @author Filip Smola
 * @version 1.0
 *
 * @deprecated Deprecated in favour of the JOML library
 */
public class Vector2f {
    //Standard vectors
    /** The zero vector */
    public static final Vector2f ZERO = new Vector2f(0, 0);
    /** The direction of the positive side of the x axis */
    public static final Vector2f AXIS_X = new Vector2f(1, 0);
    /** The direction of the positive side of the y axis */
    public static final Vector2f AXIS_Y = new Vector2f(0, 1);

    /** The x coordinate */
    public final float x;
    /** The y coordinate */
    public final float y;

    /** The magnitude squared cache (to speed up repeated access) */
    private float magnitudeSquared;

    /**
     * Constructs the vector from its coordinates
     *
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public Vector2f(float x, float y){
        //Make sure there are no -0
        if(x == -0d) x = 0;
        if(y == -0d) y = 0;

        this.x = x;
        this.y = y;
    }

    /**
     * Adds another vector to this one
     *
     * @param b The vector to add
     * @return The resulting vector
     */
    public Vector2f add(@NotNull Vector2f b){
        //Add components
        return new Vector2f(
                x + b.x,
                y + b.y
        );
    }

    /**
     * Subtracts another vector from this one
     *
     * @param b The vector to subtract
     * @return The resulting vector
     */
    public Vector2f subtract(@NotNull Vector2f b){
        //Subtract components
        return new Vector2f(
                x - b.x,
                y - b.y
        );
    }

    /**
     * Scales this vector by a factor
     *
     * @param f The factor
     * @return The resulting vector
     */
    public Vector2f scale(float f){
        //Scale the components
        return new Vector2f(
                f * x,
                f * y
        );
    }

    /**
     * Returns the unit vector with the same direction
     *
     * @return The unit vector with the same direction
     */
    public Vector2f normalize(){
        //TODO decide what to do with zero vector - let it throw ArithmeticException or return zero vector?
        return new Vector2f(
                x / magnitude(),
                y / magnitude()
        );
    }

    /**
     * Returns the magnitude of the vector
     *
     * @return The magnitude of the vector
     */
    public float magnitude(){
        return (float) Math.sqrt(magnitudeSquared());
    }

    /**
     * Returns the magnitude of the vector squared
     *
     * @return The magnitude of the vector squared
     */
    public float magnitudeSquared(){
        if(magnitudeSquared == 0 && !isZero()){
            magnitudeSquared = x*x + y*y;
        }

        return magnitudeSquared;
    }

    /**
     * Calculates the dot product of this vector and another one
     *
     * @param b The other vector
     * @return The dot product
     */
    public float dot(@NotNull Vector2f b){
        return x * b.x + y * b.y;
    }

    /**
     * Returns whether this vector is a multiple of another one
     *
     * @param b The vector to test against
     * @return Whether this vector is a multiple of the other one
     */
    public boolean isMultipleOf(@NotNull Vector2f b){
        return (dot(b) * dot(b)) / b.magnitudeSquared() == magnitudeSquared();
    }

    /**
     * Returns the projection of this vector onto another one
     *
     * @param b The vector to project onto
     * @return The projection
     */
    public Vector2f projectOnto(@NotNull Vector2f b){
        float a = dot(b) / b.magnitudeSquared();
        return b.scale(a);
    }

    /**
     * Returns the perpendicular component of this vector to another one
     *
     * @param b The other vector
     * @return The perpendicular component
     */
    public Vector2f perpComponent(@NotNull Vector2f b){
        return subtract(projectOnto(b));
    }

    /**
     * Returns the angle between this vector and another
     *
     * @param b The other vector
     * @return The angle
     */
    public double angle(@NotNull Vector2f b){
        return Math.acos(dot(b) / (magnitude() * b.magnitude()));
    }

    /**
     * Returns whether this vector is the zero vector
     *
     * @return Whether this vector is the zero vector
     */
    public boolean isZero(){
        return x == 0 && y == 0;
    }

    /**
     * Returns the x component of the vector
     *
     * @return The x component
     */
    public Vector2f getComponentX(){
        return new Vector2f(x, 0);
    }

    /**
     * Returns the y component of the vector
     *
     * @return The y component
     */
    public Vector2f getComponentY(){
        return new Vector2f(0, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector2f vector2f = (Vector2f) o;

        if (Float.compare(vector2f.x, x) != 0) return false;
        return Float.compare(vector2f.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("[%f, %f]", x, y);
    }
}
