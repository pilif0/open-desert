package net.pilif0.open_desert.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * A set of unit tests for the {@code Color} class
 *
 * @author Filip Smola
 * @version 1.0
 */
public class ColorTest {

    @Test
    public void testConstructors() throws Exception {
        assertEquals(0x00_00_00_00, (new Color(0f, 0f, 0f , 0f)).getInt());
        assertEquals(0x00_00_00_ff, (new Color(0f, 0f, 0f)).getInt());
        assertEquals(0x00_00_80_00, (new Color(0f, 0f, 0.5f , 0f)).getInt());
        assertEquals(0xff_ff_00_00, (new Color(1f, 1f, 0f , 0f)).getInt());
        assertEquals(0xff_ff_00_00, (new Color(new Color(1f, 1f, 0f , 0f).getInt()).getInt()));
    }

    @Test
    public void testGetters() throws Exception {
        Color case1 = new Color(0xff_00_00_00);
        Color case2 = new Color(0xff_00_ff_00);
        Color case3 = new Color(0x00_ff_00_ff);

        //Red
        assertEquals(1f, case1.getRed(), Float.MIN_NORMAL);
        assertEquals(1f, case2.getRed(), Float.MIN_NORMAL);
        assertEquals(0f, case3.getRed(), Float.MIN_NORMAL);

        //Green
        assertEquals(0f, case1.getGreen(), Float.MIN_NORMAL);
        assertEquals(0f, case2.getGreen(), Float.MIN_NORMAL);
        assertEquals(1f, case3.getGreen(), Float.MIN_NORMAL);

        //Blue
        assertEquals(0f, case1.getBlue(), Float.MIN_NORMAL);
        assertEquals(1f, case2.getBlue(), Float.MIN_NORMAL);
        assertEquals(0f, case3.getBlue(), Float.MIN_NORMAL);

        //Alpha
        assertEquals(0f, case1.getAlpha(), Float.MIN_NORMAL);
        assertEquals(0f, case2.getAlpha(), Float.MIN_NORMAL);
        assertEquals(1f, case3.getAlpha(), Float.MIN_NORMAL);
    }

    @Test
    public void testSetters() throws Exception {
        Color case1 = new Color(0xff_00_00_00);
        Color case2 = new Color(0xff_00_ff_00);
        Color case3 = new Color(0x00_ff_00_ff);

        //Red
        case1.setRed(0f);
        case2.setRed(0.5f);
        case3.setRed(0.125f);
        assertEquals(0x00_00_00_00, case1.getInt());
        assertEquals(0x80_00_ff_00, case2.getInt());
        assertEquals(0x20_ff_00_ff, case3.getInt());

        //Green
        case1.setGreen(0f);
        case2.setGreen(0.5f);
        case3.setGreen(0.125f);
        assertEquals(0x00_00_00_00, case1.getInt());
        assertEquals(0x80_80_ff_00, case2.getInt());
        assertEquals(0x20_20_00_ff, case3.getInt());

        //Blue
        case1.setBlue(0f);
        case2.setBlue(0.5f);
        case3.setBlue(0.125f);
        assertEquals(0x00_00_00_00, case1.getInt());
        assertEquals(0x80_80_80_00, case2.getInt());
        assertEquals(0x20_20_20_ff, case3.getInt());

        //Alpha
        case1.setAlpha(0f);
        case2.setAlpha(0.5f);
        case3.setAlpha(0.125f);
        assertEquals(0x00_00_00_00, case1.getInt());
        assertEquals(0x80_80_80_80, case2.getInt());
        assertEquals(0x20_20_20_20, case3.getInt());
    }
}
