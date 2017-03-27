package net.pilif0.open_desert.window;

import net.pilif0.open_desert.events.Event;

/**
 * Represents a window focus event
 *
 * @author Filip Smola
 * @version 1.0
 */
public class WindowFocusEvent implements Event {
    /** The related window's handle */
    public final long window;
    /** Whether the window is focused */
    public final boolean focused;

    /**
     * Constructs the event from the data given by GLFW
     *
     * @param window The related window's handle
     * @param focused Whether the window is focused
     */
    public WindowFocusEvent(long window, boolean focused){
        this.window = window;
        this.focused = focused;
    }
}
