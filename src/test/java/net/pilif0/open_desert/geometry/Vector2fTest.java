package net.pilif0.open_desert.geometry;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static net.pilif0.open_desert.geometry.Vector2f.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * A set of unit tests for the {@code Vector2f} class
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Vector2fTest {

    @Test
    public void testAdd() throws Exception {
        //Tests vector addition
        assertEquals(
                new Vector2f(1, 1),
                (new Vector2f(1, 0)).add(new Vector2f(0, 1))
        );
        assertEquals(
                new Vector2f(1, 3),
                (new Vector2f(1, 1))
                        .add(new Vector2f(0, 1))
                        .add(new Vector2f(0, 1))
        );
        assertEquals(
                new Vector2f(0, 0),
                (new Vector2f(-2, 3)).add(new Vector2f(2, -3))
        );
    }

    @Test
    public void testSubtract() throws Exception {
        //Tests vector subtraction
        assertEquals(
                new Vector2f(1, -1),
                (new Vector2f(1, 0)).subtract(new Vector2f(0, 1))
        );
        assertEquals(
                new Vector2f(0, 0),
                (new Vector2f(1, 1))
                        .subtract(new Vector2f(0, 1))
                        .subtract(new Vector2f(1, 0))
        );
        assertEquals(
                new Vector2f(-4, 6),
                (new Vector2f(-2, 3)).subtract(new Vector2f(2, -3))
        );
    }

    @Test
    public void testScale() throws Exception {
        //Test vector scaling
        assertEquals(
                new Vector2f(2, 4),
                (new Vector2f(1, 2)).scale(2)
        );
        assertEquals(
                new Vector2f(0, 0),
                (new Vector2f(1, 2)).scale(0)
        );
        assertEquals(
                new Vector2f(-1, -2),
                (new Vector2f(1, 2)).scale(-1)
        );
    }

    @Test
    public void testNormalize() throws Exception {
        //Test vector normalisation
        assertEquals(
                AXIS_Y,
                (new Vector2f(0, 15)).normalize()
        );
        assertEquals(
                new Vector2f(0, -1),
                (new Vector2f(0, -3)).normalize()
        );
    }

    @Test
    public void testMagnitude() throws Exception {
        //Test vector magnitude
        assertEquals(
                1,
                AXIS_X.magnitude(),
                Float.MIN_NORMAL
        );
        assertEquals(
                0,
                ZERO.magnitude(),
                Float.MIN_NORMAL
        );
        assertEquals(
                5,
                (new Vector2f(3, 4)).magnitude(),
                Float.MIN_NORMAL
        );
    }

    @Test
    public void testDot() throws Exception {
        //Test vector dot product
        assertEquals(
                0,
                AXIS_X.dot(AXIS_Y),
                Float.MIN_NORMAL
        );
        assertEquals(
                12,
                (new Vector2f(1, 2)).dot(new Vector2f(6, 3)),
                Float.MIN_NORMAL
        );
        assertEquals(
                2,
                (new Vector2f(1, 2)).dot(new Vector2f(0, 1)),
                Float.MIN_NORMAL
        );
    }

    @Test
    public void testIsMultipleOf() throws Exception {
        //Test vector multiple verification
        assertTrue((new Vector2f(3, 0)).isMultipleOf(AXIS_X));
        assertTrue((new Vector2f(3, 3)).isMultipleOf(new Vector2f(1, 1)));
        assertTrue((new Vector2f(3, 0)).isMultipleOf(new Vector2f(5, 0)));

        assertFalse((new Vector2f(3, 0)).isMultipleOf(AXIS_Y));
        assertFalse((new Vector2f(3, 3)).isMultipleOf(AXIS_Y));
        assertFalse((new Vector2f(3, 0)).isMultipleOf(AXIS_Y));
    }

    @Test
    public void testProjectOnto() throws Exception {
        //Test vector projection onto one vector
        assertEquals(
                new Vector2f(2, 0),
                (new Vector2f(2, 2))
                        .projectOnto(AXIS_X)
        );
        assertEquals(
                new Vector2f(0, 0),
                (new Vector2f(-2, 2))
                        .projectOnto(new Vector2f(2, 2))
        );
        assertEquals(
                new Vector2f(2, 0),
                (new Vector2f(2, 2))
                        .projectOnto(AXIS_X)
        );
    }

    @Test
    public void testPerpComponent() throws Exception {
        //Test perpendicular component calculation with respect to one vector
        assertEquals(
                new Vector2f(0, 0),
                (new Vector2f(2, 0))
                        .perpComponent(AXIS_X)
        );
        assertEquals(
                new Vector2f(2, 0),
                (new Vector2f(2, 0))
                        .perpComponent(AXIS_Y)
        );
    }

    @Test
    public void testAngle() throws Exception {
        //Test calculation of the angle between two vectors
        assertEquals(Math.toRadians(90), AXIS_X.angle(AXIS_Y), 10e-5);
        assertEquals(Math.toRadians(0), AXIS_X.angle(AXIS_X), 10e-5);
        assertEquals(Math.toRadians(45), AXIS_X.angle(new Vector2f(1, 1)), 10e-5);
    }

    @Test
    public void testComponentX() throws Exception {
        //Test calculation of the x component
        assertEquals(new Vector2f(7, 0), (new Vector2f(7, 15)).getComponentX());
        assertEquals(new Vector2f(0, 0), (new Vector2f(0, 13)).getComponentX());
    }

    @Test
    public void testComponentY() throws Exception {
        //Test calculation of the y component
        assertEquals(new Vector2f(0, 15), (new Vector2f(7, 15)).getComponentY());
        assertEquals(new Vector2f(0, 0), (new Vector2f(13, 0)).getComponentY());
    }
}
