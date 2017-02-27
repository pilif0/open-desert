package net.pilif0.open_desert.geometry;

import net.pilif0.open_desert.geometry.Vector3f;
import org.junit.Test;

import java.util.stream.DoubleStream;

import static net.pilif0.open_desert.geometry.Vector3f.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * A set of unit tests for the {@code Vector3f} class
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Vector3fTest {

    @Test
    public void testAdd() throws Exception {
        //Tests vector addition
        assertEquals(
                new Vector3f(1, 1, 1),
                (new Vector3f(1, 0, 1)).add(new Vector3f(0, 1, 0))
        );
        assertEquals(
                new Vector3f(1, 2, 3),
                (new Vector3f(1, 1, 1))
                        .add(new Vector3f(0, 1, 1))
                        .add(new Vector3f(0, 0, 1))
        );
        assertEquals(
                new Vector3f(0, 0, 0),
                (new Vector3f(-2, 3, -5)).add(new Vector3f(2, -3, 5))
        );
    }

    @Test
    public void testSubtract() throws Exception {
        //Tests vector subtraction
        assertEquals(
                new Vector3f(1, -1, 1),
                (new Vector3f(1, 0, 1)).subtract(new Vector3f(0, 1, 0))
        );
        assertEquals(
                new Vector3f(1, 0, -1),
                (new Vector3f(1, 1, 1))
                        .subtract(new Vector3f(0, 1, 1))
                        .subtract(new Vector3f(0, 0, 1))
        );
        assertEquals(
                new Vector3f(-4, 6, -10),
                (new Vector3f(-2, 3, -5)).subtract(new Vector3f(2, -3, 5))
        );
    }

    @Test
    public void testScale() throws Exception {
        //Test vector scaling
        assertEquals(
                new Vector3f(2, 4, 6),
                (new Vector3f(1, 2, 3)).scale(2)
        );
        assertEquals(
                new Vector3f(0, 0, 0),
                (new Vector3f(1, 2, 3)).scale(0)
        );
        assertEquals(
                new Vector3f(-1, -2, -3),
                (new Vector3f(1, 2, 3)).scale(-1)
        );
    }

    @Test
    public void testCross() throws Exception {
        //Test cross product of vectors
        assertEquals(
                AXIS_Z,
                AXIS_X.cross(AXIS_Y)
        );
        assertEquals(
                ZERO,
                AXIS_X.cross(AXIS_X)
        );
        assertEquals(
                new Vector3f(0, 0, -8),
                (new Vector3f(2, 2, 0)).cross(new Vector3f(2, -2, 0))
        );
    }

    @Test
    public void testNormalize() throws Exception {
        //Test vector normalisation
        assertEquals(
                AXIS_Z,
                (new Vector3f(0, 0, 13)).normalize()
        );
        assertEquals(
                new Vector3f(0, -1, 0),
                (new Vector3f(0, -3, 0)).normalize()
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
                (new Vector3f(3, 4, 0)).magnitude(),
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
                (new Vector3f(1, 2, 3)).dot(new Vector3f(6, 3, 0)),
                Float.MIN_NORMAL
        );
        assertEquals(
                -1,
                (new Vector3f(1, 2, 3)).dot(new Vector3f(0, 1, -1)),
                Float.MIN_NORMAL
        );
    }

    @Test
    public void testIsMultipleOf() throws Exception {
        //Test vector multiple verification
        assertTrue((new Vector3f(3, 0, 0)).isMultipleOf(AXIS_X));
        assertTrue((new Vector3f(3, 3, 0)).isMultipleOf(new Vector3f(1, 1, 0)));
        assertTrue((new Vector3f(3, 0, 3)).isMultipleOf(new Vector3f(5, 0, 5)));

        assertFalse((new Vector3f(3, 0 ,0)).isMultipleOf(AXIS_Y));
        assertFalse((new Vector3f(3, 3 ,0)).isMultipleOf(AXIS_Y));
        assertFalse((new Vector3f(3, 0 ,3)).isMultipleOf(AXIS_Y));
    }

    @Test
    public void testProjectOnto1D() throws Exception {
        //Test vector projection onto one vector
        assertEquals(
                new Vector3f(2, 0, 0),
                (new Vector3f(2, 2, 15))
                        .projectOnto(AXIS_X)
        );
        assertEquals(
                new Vector3f(0, 0, 0),
                (new Vector3f(-2, 2, 0))
                        .projectOnto(new Vector3f(2, 2, 0))
        );
        assertEquals(
                new Vector3f(2, 0, 0),
                (new Vector3f(2, 2, 15))
                        .projectOnto(AXIS_X)
        );
    }

    @Test
    public void testProjectOnto2D() throws Exception {
        //Test vector projection onto two vectors
        assertEquals(
                new Vector3f(2, 2, 0),
                (new Vector3f(2, 2, 42))
                    .projectOnto(AXIS_X, AXIS_Y)
        );
        assertEquals(
                new Vector3f(2, 0, 42),
                (new Vector3f(2, 2, 42))
                    .projectOnto(AXIS_X, AXIS_Z)
        );
        assertEquals(
                new Vector3f(0, 2, 42),
                (new Vector3f(2, 2, 42))
                    .projectOnto(AXIS_Y, AXIS_Z)
        );
    }

    @Test
    public void testPerpComponent1D() throws Exception {
        //Test perpendicular component calculation with respect to one vector
        assertEquals(
                new Vector3f(0, 0, 42),
                (new Vector3f(2, 0,42))
                    .perpComponent(AXIS_X)
        );
        assertEquals(
                new Vector3f(2, 0, 0),
                (new Vector3f(2, 0,42))
                    .perpComponent(AXIS_Z)
        );
        assertEquals(
                new Vector3f(1, 1, 0),
                (new Vector3f(1, 1,42))
                    .perpComponent(AXIS_Z)
        );
    }

    @Test
    public void testPerpComponent2D() throws Exception {
        //Test perpendicular component calculation with respect to one vector
        assertEquals(
                new Vector3f(0, 0, 42),
                (new Vector3f(2, 2,42))
                        .perpComponent(AXIS_X, AXIS_Y)
        );
        assertEquals(
                new Vector3f(2, 0, 0),
                (new Vector3f(2, 13,42))
                        .perpComponent(AXIS_Y, AXIS_Z)
        );
        assertEquals(
                new Vector3f(0, 1, 0),
                (new Vector3f(31, 1,42))
                        .perpComponent(AXIS_X, AXIS_Z)
        );
    }

    @Test
    public void testAngle() throws Exception {
        //Test calculation of the angle between two vectors
        assertEquals(Math.toRadians(90), AXIS_X.angle(AXIS_Y), 10e-5);
        assertEquals(Math.toRadians(0), AXIS_X.angle(AXIS_X), 10e-5);
        assertEquals(Math.toRadians(45), AXIS_X.angle(new Vector3f(1, 1, 0)), 10e-5);
    }
}
