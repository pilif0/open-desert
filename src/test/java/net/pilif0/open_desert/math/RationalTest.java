package net.pilif0.open_desert.math;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import sun.awt.RepaintArea;

import static org.junit.Assert.assertEquals;
import static net.pilif0.open_desert.math.Rational.Rationals.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * A set of unit tests for the {@code Rational} class
 *
 * @see net.pilif0.open_desert.math.Rational
 * @author Filip Smola
 * @version 1.0
 */
public class RationalTest {
    /** The maximum delta for which both floats are still equal */
    public static final float FLOAT_DELTA = Float.MIN_NORMAL;
    /** The maximum delat for which both doubles are stll equal*/
    public static final double DOUBLE_DELTA = Double.MIN_NORMAL;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testConstructorDivisionByZero() throws Exception {
        //Test that constructor with denominator 0 throws an exception
        exception.expect(ArithmeticException.class);
        new Rational(1, 0);
    }

    @Test
    public void testConstructorOverflowNum() throws Exception {
        //Test overflow in numerator
        exception.expect(ArithmeticException.class);
        new Rational(Integer.MIN_VALUE, -1);
    }

    @Test
    public void testConstructorOverflowDen() throws Exception {
        //Test overflow in denominator
        exception.expect(ArithmeticException.class);
        new Rational(1, Integer.MIN_VALUE);
    }

    @Test
    public void testConstructorSign() throws Exception {
        //Test that the constructor keeps the convention on positive denominator
        assertEquals(new Rational(-1, 1), new Rational(1, -1));
        assertEquals(new Rational(-2, 8), new Rational(2, -8));
        assertEquals(new Rational(-3, 13), new Rational(3, -13));
    }

    @Test
    public void testAddRationals() throws Exception {
        //Test addition of rationals
        assertEquals(TWO, ONE.add(ONE));
        assertEquals(new Rational(13, 40), ONE_EIGHT.add(ONE_FIFTH));
        assertEquals(ZERO, MINUS_ONE.add(ONE));
        assertEquals(ONE_EIGHT, ZERO.add(ONE_EIGHT));
    }

    @Test
    public void testAddIntegers() throws Exception {
        //Test that the result carries correctly over from the Rational overload
        assertEquals(TWO, ONE.add(1));
        assertEquals(ZERO, MINUS_ONE.add(1));
        assertEquals(new Rational(33, 8), ONE_EIGHT.add(4));
    }

    @Test
    public void testAddOverflowNum() throws Exception {
        //Test that addition throws an exception when the result would be an overflow
        exception.expect(ArithmeticException.class);
        (new Rational(Integer.MAX_VALUE, 1)).add(new Rational(Integer.MAX_VALUE, 1));
    }

    @Test
    public void testAddOverflowDen() throws Exception {
        //Test that addition throws an exception when the result would be an overflow
        exception.expect(ArithmeticException.class);
        (new Rational(1, Integer.MAX_VALUE - 1)).add(new Rational(1, Integer.MAX_VALUE));
    }

    @Test
    public void testReciprocal() throws Exception {
        //Test that it switches numerator and denominator
        assertEquals(new Rational(5, 1), ONE_FIFTH.reciprocal());
        assertEquals(new Rational(3, 1), ONE_THIRD.reciprocal());
        assertEquals(new Rational(4, 3), (new Rational(3, 4)).reciprocal());
        assertEquals(new Rational(31, 13), (new Rational(13, 31)).reciprocal());
    }

    @Test
    public void testInverse() throws Exception {
        //Test that it inverses the sign
        assertEquals(ONE, MINUS_ONE.inverse());
        assertEquals(MINUS_ONE, ONE.inverse());
        assertEquals(new Rational(2, 15), (new Rational(-2, 15)).inverse());
        assertEquals(new Rational(1, 17), (new Rational(-1, 17)).inverse());
        assertEquals(new Rational(2, 15), (new Rational(-2, 15)).inverse());
    }

    @Test
    public void testMultiplyRationals() throws Exception {
        //Test multiplication of rationals
        assertEquals(new Rational(-1, 2), MINUS_ONE.multiply(ONE_HALF));
        assertEquals(ONE, TWO.multiply(ONE_HALF));
        assertEquals(ONE, ONE_THIRD.multiply(new Rational(3, 1)));
        assertEquals(new Rational(21, 22), (new Rational(7, 11)).multiply(new Rational(3, 2)));
    }

    @Test
    public void testMultiplyIntegers() throws Exception {
        //Test that the result carries correctly over from the Rational overload
        assertEquals(new Rational(-1, 2), ONE_HALF.multiply(-1));
        assertEquals(ONE, ONE_HALF.multiply(2));
        assertEquals(ONE, ONE_THIRD.multiply(3));
    }

    @Test
    public void testMultiplyOverflowNum() throws Exception {
        //Test that multiplication throws an exception when the result would be an overflow
        exception.expect(ArithmeticException.class);
        (new Rational(Integer.MAX_VALUE, 1)).multiply(new Rational(Integer.MAX_VALUE, 1));
    }

    @Test
    public void testMultipyOverflowDen() throws Exception {
        //Test that multiplication throws an exception when the result would be an overflow
        exception.expect(ArithmeticException.class);
        (new Rational(1, Integer.MAX_VALUE)).multiply(new Rational(1, Integer.MAX_VALUE));
    }

    @Test
    public void testDivideRationals() throws Exception {
        //Test division of rationals
        assertEquals(ONE_HALF, ONE.divide(TWO));
        assertEquals(new Rational(4, 1), ONE_HALF.divide(ONE_EIGHT));
        assertEquals(new Rational(1, 16), ONE_HALF.divide(new Rational(8)));
    }

    @Test
    public void testDivideIntegers() throws Exception {
        //Test that the result carries correctly over from the Rational overload
        assertEquals(ONE_HALF, ONE.divide(2));
        assertEquals(new Rational(1, 16), ONE_HALF.divide(8));
        assertEquals(new Rational(31, 26), (new Rational(31,2)).divide(13));
    }

    @Test
    public void testCompare() throws Exception {
        //Test comparison of Rationals
        assertTrue(Rational.compare(ONE, MINUS_ONE) > 0);
        assertTrue(Rational.compare(ONE_HALF, ONE_EIGHT) > 0);
        assertTrue(Rational.compare(ONE_THIRD, ONE_HALF) < 0);
        assertTrue(Rational.compare(ONE_HALF, new Rational(1, 2)) == 0);
    }

    @Test
    public void testSignum() throws Exception {
        //Test signum function of Rationals
        assertEquals(-1, MINUS_ONE.signum());
        assertEquals(0, ZERO.signum());
        assertEquals(1, ONE.signum());
        assertEquals(-1, (new Rational(-2,3)).signum());
    }

    @Test
    public void testIsWhole() throws Exception {
        //Test whether certain Rationals are whole
        assertTrue(ONE.isWhole());
        assertTrue(TWO.isWhole());
        assertTrue((new Rational(8, 4)).isWhole());

        assertFalse(ONE_HALF.isWhole());
        assertFalse(ONE_THIRD.isWhole());
        assertFalse((new Rational(8, 3)).isWhole());
    }

    @Test
    public void testEquals() throws Exception {
        //Test equality of Rationals
        assertTrue(ONE.equals(ONE));
        assertTrue(ONE.equals(new Rational(1)));
        assertTrue((new Rational(1, 8)).equals(ONE_EIGHT));

        assertFalse(ONE.equals(MINUS_ONE));
        assertFalse(ONE.equals(new Rational(2)));
        assertFalse((new Rational(2, 13)).equals(ONE_EIGHT));
    }

    @Test
    public void testIntValue() throws Exception {
        //Test proper rounding
        assertEquals(1, ONE_HALF.intValue());
        assertEquals(0, ONE_THIRD.intValue());
        assertEquals(1, (new Rational(2, 3)).intValue());
        assertEquals(-1, (new Rational(-1, 2)).intValue());
        assertEquals(0, (new Rational(-1, 3)).intValue());
    }

    //longValue() directly delegates to intValue() therefore no test is needed

    @Test
    public void testFloatValue() throws Exception {
        //Test proper division
        assertEquals(0.5f, ONE_HALF.floatValue(), FLOAT_DELTA);
        assertEquals(1f, ONE.floatValue(), FLOAT_DELTA);
        assertEquals(0.125f, ONE_EIGHT.floatValue(), FLOAT_DELTA);
    }

    @Test
    public void testDoubleValue() throws Exception {
        //Test proper division
        assertEquals(0.5d, ONE_HALF.doubleValue(), DOUBLE_DELTA);
        assertEquals(1d, ONE.doubleValue(), DOUBLE_DELTA);
        assertEquals(0.125d, ONE_EIGHT.doubleValue(), DOUBLE_DELTA);
    }

    //compareTo(Rational) directly delegates to compare(Rational, Rational) therefore no test is needed

    @Test
    public void testToString() throws Exception {
        //Test proper conversion to String
        assertEquals("(1 / 1)", ONE.toString());
        assertEquals("(1 / 8)", ONE_EIGHT.toString());
        assertEquals("(13 / 31)", (new Rational(13, 31)).toString());
        assertEquals("(1 / 2)", (new Rational(2, 4)).toString());
        assertEquals("(-1 / 1)", MINUS_ONE.toString());
    }
}
