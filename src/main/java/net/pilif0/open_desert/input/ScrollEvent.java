package net.pilif0.open_desert.input;

import net.pilif0.open_desert.events.Event;

/**
 * Represents a single scroll event
 *
 * @author Filip Smola
 * @version 1.0
 */
public class ScrollEvent implements Event {
    /** The handle of the associated window */
    public final long window;
    /** The horizontal offset */
    public final double x;
    /** The vertical offset */
    public final double y;

    /**
     * Constructs the event from the data given by GLFW
     *
     * @param window The handle of the associated window
     * @param xoffset The horizontal offset
     * @param yoffset The vertical offset
     */
    public ScrollEvent(long window, double xoffset, double yoffset){
        this.window = window;
        this.x = xoffset;
        this.y = yoffset;
    }

    /**
     * Constructs the event from the data of another event (copy)
     *
     * @param source Source event
     */
    public ScrollEvent(ScrollEvent source){
        this.window = source.window;
        this.x = source.x;
        this.y = source.y;
    }
}
