package net.pilif0.open_desert.geometry;

import org.joml.Matrix4f;
import org.joml.Vector3fc;
import org.joml.Vector3f;

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
    private final Vector3f translation;
    /** The scale along the axes */
    private final Vector3f scale;
    /** The rotation around the axes (in radians) */
    private final Vector3f rotation;
    /** The matrix representation */
    private final Matrix4f matrix;
    /** Whether the transformation was changed since the last time the matrix was generated */
    private boolean transformed = false;

    /**
     * Constructs an empty (identity) transformation
     */
    public Transformation(){
        translation = new Vector3f();
        scale = (new Vector3f()).set(1);
        rotation = new Vector3f();
        matrix = (new Matrix4f()).identity();
    }

    /**
     * Constructs the transformation from all its properties
     *
     * @param translation The translation
     * @param scale The scale
     * @param rotation The rotation
     */
    public Transformation(Vector3fc translation, Vector3fc scale, Vector3fc rotation){
        this.translation = (new Vector3f()).set(translation);
        this.scale = (new Vector3f()).set(scale);
        this.rotation = (new Vector3f()).set(rotation);
        matrix = (new Matrix4f()).identity();
        regenerateMatrix();
    }

    /**
     * Translates the subject of this transformation by the vector
     *
     * @param diff The difference
     */
    public void translate(Vector3fc diff){
        translation.add(diff);
        transformed = true;
    }

    /**
     * Rotates the subject of this transformation by a certain angle around each axis
     *
     * @param angles The angles to rotate by around each axis
     */
    public void rotate(Vector3fc angles){
        rotation.add(angles);

        //Normalize components to [0, 2PI) rad
        rotation.set(rotation.x % PI2, rotation.y % PI2, rotation.z % PI2);

        transformed = true;
    }

    /**
     * Scales the subject of this transformation (by multiplying) by a certain factor along each axis
     *
     * @param factors The factors to scale by along each axis
     */
    public void scaleMul(Vector3fc factors){
        scale.mul(factors);
        transformed = true;
    }

    /**
     * Scales the subject of this transformation (by adding) by a certain factor along each axis
     *
     * @param factors The factors to scale by along each axis
     */
    public void scaleAdd(Vector3fc factors){
        scale.add(factors);
        transformed = true;
    }

    /**
     * Sets the translation
     *
     * @param translation The new translation
     */
    public void setTranslation(Vector3fc translation){
        this.translation.set(translation);
        transformed = true;
    }

    /**
     * Returns the translation
     *
     * @return The translation
     */
    public Vector3fc getTranslation(){
        return this.translation.toImmutable();
    }

    /**
     * Sets the scale
     *
     * @param scale The new scale
     */
    public void setScale(Vector3fc scale){
        this.scale.set(scale);
        transformed = true;
    }

    /**
     * Returns the scale
     *
     * @return The scale
     */
    public Vector3fc getScale(){
        return this.scale.toImmutable();
    }

    /**
     * Sets the rotation
     *
     * @param rotation The new rotation
     */
    public void setRotation(Vector3fc rotation){
        this.rotation.set(rotation);
        transformed = true;
    }

    /**
     * Returns the rotation
     *
     * @return The rotation
     */
    public Vector3fc getRotation(){
        return this.rotation.toImmutable();
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
                .translate(translation)
                .rotateXYZ(rotation)
                .scale(scale);

        //Reset the flag
        transformed = false;
    }
}
