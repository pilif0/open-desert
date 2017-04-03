package net.pilif0.open_desert.util;

import org.joml.Vector4f;

/**
 * Represents an RGBA colour (internally as an int)
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Color {
    /** The red component mask */
    public static final int RED_MASK = 0xff_00_00_00;
    /** The green component mask */
    public static final int GREEN_MASK = 0x00_ff_00_00;
    /** The blue component mask */
    public static final int BLUE_MASK = 0x00_00_ff_00;
    /** The alpha component mask */
    public static final int ALPHA_MASK = 0x00_00_00_ff;
    /** The red component bit-shift */
    public static final byte RED_SHIFT = 24;
    /** The green component bit-shift */
    public static final byte GREEN_SHIFT = 16;
    /** The blue component bit-shift */
    public static final byte BLUE_SHIFT = 8;
    /** The alpha component bit-shift */
    public static final byte ALPHA_SHIFT = 0;

    /** The colour integer */
    private int color;

    /**
     * Constructs the colour from its components. The components must be within 0-1.
     * Anything less than zero will be made zero, anything greater than one will be made one.
     *
     * @param r The red component
     * @param g The green component
     * @param b The blue component
     * @param a The alpha component
     */
    public Color(float r, float g, float b, float a){
        //Validate input
        if(r < 0) r = 0.0f; else if(r > 1) r = 1.0f;
        if(g < 0) g = 0.0f; else if(g > 1) g = 1.0f;
        if(b < 0) b = 0.0f; else if(b > 1) b = 1.0f;
        if(a < 0) a = 0.0f; else if(a > 1) a = 1.0f;

        //Build integer
        color  = Math.round(0xff * r) << RED_SHIFT;
        color += Math.round(0xff * g) << GREEN_SHIFT;
        color += Math.round(0xff * b) << BLUE_SHIFT;
        color += Math.round(0xff * a) << ALPHA_SHIFT;
    }

    /**
     * Constructs the colour from its components with full alpha. The components must be within 0-1.
     * Anything less than zero will be made zero, anything greater than one will be made one.
     *
     * @param r The red component
     * @param g The green component
     * @param b The blue component
     */
    public Color(float r, float g, float b){
        this(r, g, b, 1.0f);
    }

    /**
     * Construct the colour from a colour integer
     *
     * @param color The colour integer
     */
    public Color(int color){
        this.color = color;
    }

    /**
     * Returns the integer representation of the colour
     * 
     * @return The colour
     */
    public int getInt(){ return color; }

    /**
     * Returns the red component
     * 
     * @return The red component (within 0-1)
     */
    public float getRed(){ return ((RED_MASK & color) >>> RED_SHIFT) / 255f; }

    /**
     * Returns the green component
     *
     * @return The green component (within 0-1)
     */
    public float getGreen(){ return ((GREEN_MASK & color) >>> GREEN_SHIFT) / 255f; }

    /**
     * Returns the blue component
     *
     * @return The blue component (within 0-1)
     */
    public float getBlue(){ return ((BLUE_MASK & color) >>> BLUE_SHIFT) / 255f; }

    /**
     * Returns the alpha component
     *
     * @return The alpha component (within 0-1)
     */
    public float getAlpha(){ return (ALPHA_MASK & color >>> ALPHA_SHIFT) / 255f; }

    /**
     * Sets the red component
     * 
     * @param red The new value (must be within 0-1)
     */
    public void setRed(float red){
        if(red < 0) red = 0; else if(red > 1) red = 1;
        
        //Remove old
        color = color & ~RED_MASK;
        
        //Add new
        color += Math.round(0xff * red) << RED_SHIFT;
    }

    /**
     * Sets the green component
     *
     * @param green The new value (must be within 0-1)
     */
    public void setGreen(float green){
        if(green < 0) green = 0; else if(green > 1) green = 1;

        //Remove old
        color = color & ~GREEN_MASK;

        //Add new
        color += Math.round(0xff * green) << GREEN_SHIFT;
    }

    /**
     * Sets the blue component
     *
     * @param blue The new value (must be within 0-1)
     */
    public void setBlue(float blue){
        if(blue < 0) blue = 0; else if(blue > 1) blue = 1;

        //Remove old
        color = color & ~BLUE_MASK;

        //Add new
        color += Math.round(0xff * blue) << BLUE_SHIFT;
    }

    /**
     * Sets the alpha component
     *
     * @param alpha The new value (must be within 0-1)
     */
    public void setAlpha(float alpha){
        if(alpha < 0) alpha = 0; else if(alpha > 1) alpha = 1;

        //Remove old
        color = color & ~ALPHA_MASK;

        //Add new
        color += Math.round(0xff * alpha) << ALPHA_SHIFT;
    }

    /**
     * Returns the colour as an RGBA vector
     *
     * @return The colour as an RGBA vector
     */
    public Vector4f toVector(){
        return new Vector4f(getRed(), getGreen(), getBlue(), getAlpha());
    }
}
