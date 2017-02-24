package net.pilif0.open_desert.math;

import org.junit.Test;

import static net.pilif0.open_desert.math.HighestCommonFactor.hcf;
import static org.junit.Assert.assertEquals;

/**
 * A set of unit tests for the {@code HighestCommonFactor} class
 *
 * @see net.pilif0.open_desert.math.HighestCommonFactor
 * @author Filip Smola
 * @version 1.0
 */
public class HighestCommonFactorTest {

    @Test
    public void testHcfInt() throws Exception {
        //Test that the result carries correctly over from the long overload
        assertEquals(hcf(1L, 2L), hcf(1,2));
        assertEquals(hcf(2L, 16L), hcf(2,16));
        assertEquals(hcf(13L, 39L), hcf(13,39));
        assertEquals(hcf(-5L, 10L), hcf(-5,10));
        assertEquals(hcf(1L, 0L), hcf(1,0));
        assertEquals(hcf(13L, 31L), hcf(13,31));
    }

    @Test
    public void testHcfBasicCases() throws Exception {
        //Test some basic cases
        assertEquals(1, hcf(1L, 2L));
        assertEquals(2, hcf(2L, 16L));
        assertEquals(13, hcf(13L, 39L));
        assertEquals(5, hcf(5L, 10L));
        assertEquals(1, hcf(1L, 0L));
        assertEquals(1, hcf(13L, 31L));
    }

    @Test
    public void testHcfCommutative() throws Exception {
        //Test that hcf(a,b) = hcf(b,a)
        assertEquals(hcf(16L, 2L), hcf(2L, 16L));
        assertEquals(hcf(39L, 13L), hcf(13L, 39L));
        assertEquals(hcf(10L, 5L), hcf(5L, 10L));
        assertEquals(hcf(31L, 13L), hcf(13L, 31L));
    }

    @Test
    public void testHcfNegatives() throws Exception {
        //Test that hcf(a,b) = hcf(|a|,|b|)
        assertEquals(hcf(16L, 2L), hcf(-2L, 16L));
        assertEquals(hcf(39L, 13L), hcf(-13L, -39L));
        assertEquals(hcf(10L, 5L), hcf(5L, -10L));
        assertEquals(hcf(31L, 13L), hcf(-13L, 31L));
    }
}
