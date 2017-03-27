package net.pilif0.open_desert.window;

import net.pilif0.open_desert.events.Event;

/**
 * Represents a window close event
 *
 * @author Filip Smola
 * @version 1.0
 */
public class WindowCloseEvent implements Event {
    /** The related window's handle */
    public final long window;

    /**
     * Constructs the event from the data given by GLFW
     *
     * @param window The related window's handle
     */
    public WindowCloseEvent(long window){
        this.window = window;
    }
}
