package net.pilif0.open_desert.geometry;

import org.joml.Matrix4f;
import org.joml.Vector2fc;
import org.joml.Vector2f;

/**
 * Represents a transformation (translation, scaling, rotation)
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Transformation {
    /** The 2PI constant */
    private static final float PI2 = (float) Math.PI * 2;

    /** The translation along the axes */
    private final Vector2f translation;
    /** The scale along the axes */
    private final Vector2f scale;
    /** The rotation around the z axis (in radians) */
    private float rotation;
    /** The matrix representation */
    private final Matrix4f matrix;
    /** Whether the transformation was changed since the last time the matrix was generated */
    private boolean transformed = false;

    /**
     * Constructs an empty (identity) transformation
     */
    public Transformation(){
        translation = new Vector2f();
        rotation = 0;
        scale = (new Vector2f()).set(1);
        matrix = (new Matrix4f()).identity();
    }

    /**
     * Constructs the transformation from all its properties
     *
     * @param translation The translation
     * @param scale The scale
     * @param rotation The rotation
     */
    public Transformation(Vector2fc translation, Vector2fc scale, float rotation){
        this.translation = (new Vector2f()).set(translation);
        this.rotation = rotation % PI2;
        this.scale = (new Vector2f()).set(scale);
        matrix = (new Matrix4f()).identity();
        regenerateMatrix();
    }

    /**
     * Constructs a transformation by copying another one
     *
     * @param t The transformation to copy
     */
    public Transformation(Transformation t){
        this.translation = (new Vector2f()).set(t.getTranslation());
        this.rotation = t.getRotation();
        this.scale = (new Vector2f()).set(t.getScale());
        matrix = (new Matrix4f()).identity();
        regenerateMatrix();
    }

    /**
     * Translates the subject of this transformation by the vector
     *
     * @param diff The difference
     */
    public void translate(Vector2fc diff){
        translation.add(diff);
        transformed = true;
    }

    /**
     * Rotates the subject of this transformation by a certain angle around each axis
     *
     * @param angle The angles to rotate by around each axis
     */
    public void rotate(float angle){
        rotation += angle;

        //Normalize components to [0, 2PI) rad
        rotation = rotation % PI2;

        transformed = true;
    }

    /**
     * Scales the subject of this transformation (by multiplying) by a certain factor along each axis
     *
     * @param factors The factors to scale by along each axis
     */
    public void scaleMul(Vector2fc factors){
        scale.mul(factors);
        transformed = true;
    }

    /**
     * Scales the subject of this transformation (by adding) by a certain factor along each axis
     *
     * @param factors The factors to scale by along each axis
     */
    public void scaleAdd(Vector2fc factors){
        scale.add(factors);
        transformed = true;
    }

    /**
     * Sets the translation
     *
     * @param translation The new translation
     */
    public void setTranslation(Vector2fc translation){
        this.translation.set(translation);
        transformed = true;
    }

    /**
     * Returns the translation
     *
     * @return The translation
     */
    public Vector2fc getTranslation(){
        return this.translation.toImmutable();
    }

    /**
     * Sets the scale
     *
     * @param scale The new scale
     */
    public void setScale(Vector2fc scale){
        this.scale.set(scale);
        transformed = true;
    }

    /**
     * Returns the scale
     *
     * @return The scale
     */
    public Vector2fc getScale(){
        return this.scale.toImmutable();
    }

    /**
     * Sets the rotation
     *
     * @param rotation The new rotation
     */
    public void setRotation(float rotation){
        this.rotation = rotation % PI2;
        transformed = true;
    }

    /**
     * Returns the rotation
     *
     * @return The rotation
     */
    public float getRotation(){
        return this.rotation;
    }

    /**
     * Returns the matrix representation
     *
     * @return The matrix representation
     */
    public Matrix4f getMatrix(){
        //Regenerate the matrix if required
        if(transformed){
            regenerateMatrix();
        }

        return matrix;
    }

    /**
     * Regenerates the matrix after changing the transformation
     */
    protected void regenerateMatrix(){
        //Check whether the regeneration is needed
        if(!transformed){
            return;
        }

        //Regenerate the matrix
        matrix.identity()
                .translate(translation.x, translation.y, 0)
                .rotateZ(rotation)
                .scale(scale.x, scale.y, 1f);

        //Reset the flag
        transformed = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transformation)) return false;

        Transformation that = (Transformation) o;

        if (Float.compare(rotation, that.rotation) != 0) return false;
        if (!translation.equals(that.translation)) return false;
        return scale.equals(that.scale);
    }

    @Override
    public int hashCode() {
        int result = translation.hashCode();
        result = 31 * result + scale.hashCode();
        result = 31 * result + (rotation != +0.0f ? Float.floatToIntBits(rotation) : 0);
        return result;
    }
}
