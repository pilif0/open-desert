package net.pilif0.open_desert.math;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

/**
 * Represents a rational number (quotient of two integers).
 * The objects are meant as immutable and manipulation creates new instances.
 * As a convention, denominator will always be kept positive.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Rational extends Number implements Comparable<Rational> {
    /** The numerator */
    public final int numerator;
    /** The denominator (always positive by convention) */
    public final int denominator;

    /**
     * Constructs the rational representation of an integer
     *
     * @param n The integer
     */
    public Rational(int n){
        this(n, 1);
    }

    /**
     * Constructs the rational from the numerator and denominator
     *
     * @param num The numerator
     * @param den The denominator
     * @throws ArithmeticException On division by zero or on possible overflow (negative denominator and extreme values)
     */
    public Rational(int num, int den){
        //Check division by zero
        if(den == 0){
            throw new ArithmeticException("Division by zero");
        }

        //Check that the numbers are in reduced form
        int hcf = HighestCommonFactor.hcf(num, den);
        if(hcf > 1){
            //Case: not reduced

            num /= hcf;
            den /= hcf;
        }

        //Check negative denominator
        if(den < 0){
            //Check possible overflow (MIN_VALUE * (-1) == MIN_VALUE)
            //TODO at a later point, check performance without the following two checks
            if(num == Integer.MIN_VALUE){
                //TODO deal with overflow (something better than exception?)
                throw new ArithmeticException("The numerator is Integer.MIN_VALUE and denominator is negative, which would result in an overflow.");
            }
            if(den == Integer.MIN_VALUE){
                //TODO deal with overflow (something better than exception?)
                throw new ArithmeticException("The denominator is Integer.MIN_VALUE (and therefore negative), which would result in an overflow.");
            }

            //Invert both components
            num *= -1;
            den *= -1;
        }

        //Set the fields
        numerator = num;
        denominator = den;
    }

    /**
     * Adds a rational to this one
     *
     * @param b The rational to add
     * @return The result of the addition
     * @throws ArithmeticException On a possible overflow
     */
    public Rational add(@NotNull Rational b){
        //Calculate the new numerators and denominators
        long newNum = ((long) numerator * (long) b.denominator) + ((long) b.numerator * (long) denominator);
        long newDen = (long) denominator * (long) b.denominator;

        //Reduce
        long hcf = HighestCommonFactor.hcf(newNum, newDen);
        if(hcf > 1){
            newNum /= hcf;
            newDen /= hcf;
        }

        //Try to convert both the new values to integers
        int num = 0;
        int den = 0;
        try{
            num = Math.toIntExact(newNum);
            den = Math.toIntExact(newDen);
        }catch(ArithmeticException e){
            throw new ArithmeticException("Overflow when adding rationals (new numerator = "+newNum+", new denominator = "+newDen+")");
        }

        //Return the new value
        return new Rational(num, den);
    }

    /**
     * Adds an integer to this rational
     *
     * @param b The integer to add
     * @return The result of the addition
     */
    public Rational add(int b){
        //Add the rational b/1 to this one
        return add(new Rational(b));
    }

    /**
     * Returns the reciprocal of this rational (a/b -> b/a)
     *
     * @return The reciprocal of this rational
     */
    public Rational reciprocal(){
        return new Rational(denominator, numerator);
    }

    /**
     * Returns the additive inverse of this rational (a/b -> -a/b)
     *
     * @return The additive inverse of this rational
     * @throws ArithmeticException On a possible overflow
     */
    public Rational inverse(){
        //Check possible overflow
        if(numerator == Integer.MIN_VALUE){
            //TODO deal with overflow (something better than exception?)
            throw new ArithmeticException("The numerator is Integer.MIN_VALUE, therefore inversion would result in an overflow.");
        }

        return new Rational((-1) * numerator, denominator);
    }

    /**
     * Multiplies this rational by another one
     *
     * @param b The rational to multiply
     * @return The result of the multiplication
     */
    public Rational multiply(@NotNull Rational b){
        //Skip when one numerator is the other denominator
        if(numerator == b.denominator){
            return new Rational(b.numerator, denominator);
        }
        if(b.numerator == denominator){
            return new Rational(numerator, b.denominator);
        }

        //Multiply the numerators and the denominators
        long newNum = (long) numerator * (long) b.numerator;
        long newDen = (long) denominator * (long) b.denominator;

        //Reduce
        long hcf = HighestCommonFactor.hcf(newNum, newDen);
        if(hcf > 1){
            newNum /= hcf;
            newDen /= hcf;
        }

        //Try to convert both the new values to integers
        int num = 0;
        int den = 0;
        try{
            num = Math.toIntExact(newNum);
            den = Math.toIntExact(newDen);
        }catch(ArithmeticException e){
            throw new ArithmeticException("Overflow when multiplying rationals (newNum = "+newNum+", newDen = "+newDen+")");
        }

        //Return the new value
        return new Rational(num, den);
    }

    /**
     * Multiplies this rational by an integer
     *
     * @param b The integer to multiply
     * @return The result of the multiplication
     */
    public Rational multiply(int b){
        //Skip when b is the denominator
        if(b == denominator){
            return new Rational(numerator);
        }

        //Multiply by rational b/1
        return multiply(new Rational(b, 1));
    }

    /**
     * Divides this rational by another one
     *
     * @param b The rational to divide by
     * @return The result of the division
     */
    public Rational divide(@NotNull Rational b){
        //Multiply by the reciprocal
        return multiply(b.reciprocal());
    }

    /**
     * Divides this rational by an integer
     *
     * @param b The integer to divide by
     * @return The result of the division
     * @throws ArithmeticException On division by zero
     */
    public Rational divide(int b){
        //Multiply by 1/b
        return multiply(new Rational(1, b));
    }

    /**
     * Compares two rationals
     *
     * @param a The first rational
     * @param b The second rational
     * @return The value {@code 0} if {@code a == b};
     *      a value less than {@code 0} if {@code a < b};
     *      and a value greater than {@code 0} if {@code a > b}
     */
    public static int compare(@NotNull Rational a, @NotNull Rational b){
        //Compare the numerators multiplied by the other one's denominator
        return Long.compare(a.numerator * b.denominator, b.numerator * a.denominator);
    }

    /**
     * Returns the signum function of the rational (its numerator)
     *
     * @return The signum function of the rational ({@code -1}, {@code 0}, or {@code 1})
     */
    public int signum(){
        return Integer.signum(numerator);
    }

    /**
     * Returns whether the rational is a whole number (an integer)
     *
     * @return Whether the rational is a whole number
     */
    public boolean isWhole(){
        return denominator == 1;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rational rational = (Rational) o;

        if (numerator != rational.numerator) return false;
        return denominator == rational.denominator;
    }

    @Override
    public int hashCode() {
        int result = numerator;
        result = 31 * result + denominator;
        return result;
    }

    @Override
    public int intValue(){
        //Ensure symmetric rounding
        float v = floatValue();
        if(v < 0){
            //Case: value is negative

            return (-1) * Math.round(Math.abs(v));
        }else{
            //Case: value is non-negative

            return Math.round(v);
        }
    }

    @Override
    public long longValue() {
        //Extreme value cannot exceed extreme integer values
        return intValue();
    }

    @Override
    public float floatValue() {
        return (float) numerator / (float) denominator;
    }

    @Override
    public double doubleValue() {
        return (double) numerator / (double) denominator;
    }

    @Override
    public int compareTo(Rational o) {
        return compare(this, o);
    }

    @Override
    public String toString() {
        return "("+numerator+" / "+denominator+")";
    }

    /**
     * A class holding commonly used rationals
     */
    public static class Rationals {
        /** Rational for -1 */
        public static final Rational MINUS_ONE = new Rational(-1);
        /** Rational for 0 */
        public static final Rational ZERO = new Rational(0);
        /** Rational for 1/8 */
        public static final Rational ONE_EIGHT = new Rational(1, 8);
        /** Rational for 1/5 */
        public static final Rational ONE_FIFTH = new Rational(1, 5);
        /** Rational for 1/4 */
        public static final Rational ONE_QUARTER = new Rational(1, 4);
        /** Rational for 1/3 */
        public static final Rational ONE_THIRD = new Rational(1, 3);
        /** Rational for 1/2 */
        public static final Rational ONE_HALF = new Rational(1, 2);
        /** Rational for 1 */
        public static final Rational ONE = new Rational(1);
        /** Rational for 2 */
        public static final Rational TWO = new Rational(2);
    }
}
