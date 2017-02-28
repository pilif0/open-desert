package net.pilif0.open_desert.geometry.planar;

import net.pilif0.open_desert.geometry.Vector2f;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * A set of unit tests for the {@code Rectangle} class
 *
 * @author Filip Smola
 * @version 1.0
 */
public class RectangleTest {

    @Test
    public void testGetCentre() throws Exception {
        //Test the centre calculation
        assertEquals(
                new Vector2f(1, 1),
                (new Rectangle(Vector2f.ZERO, 2, 2)).getCentre()
        );
        assertEquals(
                new Vector2f(0, 0),
                (new Rectangle(new Vector2f(-10, -10), 20, 20)).getCentre()
        );
        assertEquals(
                new Vector2f(3, 4),
                (new Rectangle(Vector2f.ZERO, 6, 8)).getCentre()
        );
    }

    @Test
    public void testGetCorner() throws Exception {
        //Test the corner calculation
        Rectangle subject = new Rectangle(new Vector2f(2, 3), 7, 9);
        assertEquals(
                new Vector2f(2, 3),
                subject.getCorner(Rectangle.Corner.BOT_LEFT)
        );
        assertEquals(
                new Vector2f(9, 3),
                subject.getCorner(Rectangle.Corner.BOT_RIGHT)
        );
        assertEquals(
                new Vector2f(2, 12),
                subject.getCorner(Rectangle.Corner.TOP_LEFT)
        );
        assertEquals(
                new Vector2f(9, 12),
                subject.getCorner(Rectangle.Corner.TOP_RIGHT)
        );
    }
}
