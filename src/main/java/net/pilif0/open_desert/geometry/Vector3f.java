package net.pilif0.open_desert.geometry;

/**
 * Represents a vector in three dimensions.
 *
 * @author Filip Smola
 * @version 1.0
 *
 * @deprecated Deprecated in favour of the JOML library
 */
public class Vector3f {
    //Standard vectors
    /** The zero vector */
    public static final Vector3f ZERO = new Vector3f(0, 0, 0);
    /** The direction of the positive side of the x axis */
    public static final Vector3f AXIS_X = new Vector3f(1, 0, 0);
    /** The direction of the positive side of the y axis */
    public static final Vector3f AXIS_Y = new Vector3f(0, 1, 0);
    /** The direction of the positive side of the z axis */
    public static final Vector3f AXIS_Z = new Vector3f(0, 0, 1);

    /** The x coordinate */
    public final float x;
    /** The y coordinate */
    public final float y;
    /** The z coordinate */
    public final float z;

    /** The magnitude squared cache (to speed up repeated access) */
    private float magnitudeSquared;

    /**
     * Constructs the vector from its coordinates
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @param z The z coordinate
     */
    public Vector3f(float x, float y, float z){
        //Make sure there are no -0
        if(x == -0d) x = 0;
        if(y == -0d) y = 0;
        if(z == -0d) z = 0;

        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Adds another vector to this one
     *
     * @param b The vector to add
     * @return The resulting vector
     */
    public Vector3f add(Vector3f b){
        //Add components
        return new Vector3f(
                x + b.x,
                y + b.y,
                z + b.z);
    }

    /**
     * Subtracts another vector from this one
     *
     * @param b The vector to subtract
     * @return The resulting vector
     */
    public Vector3f subtract(Vector3f b){
        //Subtract components
        return new Vector3f(
                x - b.x,
                y - b.y,
                z - b.z
        );
    }

    /**
     * Scales this vector by a factor
     *
     * @param f The factor
     * @return The resulting vector
     */
    public Vector3f scale(float f){
        //Scale the components
        return new Vector3f(
                f * x,
                f * y,
                f * z
        );
    }

    /**
     * Calculates the cross product of this vector with another one
     *
     * @param b The other vector
     * @return The cross product of the vectors
     */
    public Vector3f cross(Vector3f b){
        //Calculate the cross product this x b
        return new Vector3f(
                y * b.z - z * b.y,
                z * b.x - x * b.z,
                x * b.y - y * b.x);
    }

    /**
     * Returns the unit vector with the same direction
     *
     * @return The unit vector with the same direction
     */
    public Vector3f normalize(){
        //TODO decide what to do with zero vector - let it throw ArithmeticException or return zero vector?
        return new Vector3f(
                x / magnitude(),
                y / magnitude(),
                z / magnitude()
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
            magnitudeSquared = x*x + y*y + z*z;
        }

        return magnitudeSquared;
    }

    /**
     * Calculates the dot product of this vector and another one
     *
     * @param b The other vector
     * @return The dot product
     */
    public float dot(Vector3f b){
        return x * b.x + y * b.y + z * b.z;
    }

    /**
     * Returns whether this vector is a multiple of another one
     *
     * @param b The vector to test against
     * @return Whether this vector is a multiple of the other one
     */
    public boolean isMultipleOf(Vector3f b){
        return (dot(b) * dot(b)) / b.magnitudeSquared() == magnitudeSquared();
    }

    /**
     * Returns the projection of this vector onto another one
     *
     * @param b The vector to project onto
     * @return The projection
     */
    public Vector3f projectOnto(Vector3f b){
        float a = dot(b) / b.magnitudeSquared();
        return b.scale(a);
    }

    /**
     * Returns the projection of this vector onto a pair of other vectors (a plane)
     *
     * @param u The first vector to project onto
     * @param v The second vector to project onto
     * @return The projection
     */
    public Vector3f projectOnto(Vector3f u, Vector3f v){
        return subtract(projectOnto(u.cross(v)));
    }

    /**
     * Returns the perpendicular component of this vector to another one
     *
     * @param b The other vector
     * @return The perpendicular component
     */
    public Vector3f perpComponent(Vector3f b){
        return subtract(projectOnto(b));
    }

    /**
     * Returns the perpendicular component of this vector to other two (a plane)
     *
     * @param u The first vector
     * @param v The second vector
     * @return The perpendicular component
     */
    public Vector3f perpComponent(Vector3f u, Vector3f v){
        return projectOnto(u.cross(v));
    }

    /**
     * Returns the angle between this vector and another
     *
     * @param b The other vector
     * @return The angle
     */
    public double angle(Vector3f b){
        return Math.acos(dot(b) / (magnitude() * b.magnitude()));
    }

    /**
     * Returns whether this vector is the zero vector
     *
     * @return Whether this vector is the zero vector
     */
    public boolean isZero(){
        return x == 0 && y == 0 && z == 0;
    }

    /**
     * Returns the x component of the vector
     *
     * @return The x component
     */
    public Vector3f getComponentX(){ return new Vector3f(x, 0, 0); }

    /**
     * Returns the y component of the vector
     *
     * @return The y component
     */
    public Vector3f getComponentY(){ return new Vector3f(0, y, 0); }

    /**
     * Returns the z component of the vector
     *
     * @return The z component
     */
    public Vector3f getComponentZ(){ return new Vector3f(0, 0, z); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector3f vector3f = (Vector3f) o;

        if (Float.compare(vector3f.x, x) != 0) return false;
        if (Float.compare(vector3f.y, y) != 0) return false;
        return Float.compare(vector3f.z, z) == 0;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("[%f, %f, %f]", x, y, z);
    }
}
