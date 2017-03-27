package net.pilif0.open_desert.window;

import net.pilif0.open_desert.events.Event;

/**
 * Represents a window size event0
 *
 * @author Filip Smola
 * @version 1.0
 */
public class WindowSizeEvent implements Event {
    /** The related window's handle */
    public final long window;
    /** The new width */
    public final int width;
    /** The new height */
    public final int height;

    /**
     * Constructs the event from the data given by GLFW
     *
     * @param window The related window's handle
     * @param width The new width
     * @param height The new height
     */
    public WindowSizeEvent(long window, int width, int height){
        this.window = window;
        this.width = width;
        this.height = height;
    }
}
