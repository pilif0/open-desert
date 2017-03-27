package net.pilif0.open_desert.window;

import net.pilif0.open_desert.events.Event;

/**
 * Represents a window position event
 *
 * @author Filip Smola
 * @version 1.0
 */
public class WindowPosEvent implements Event {
    /** The related window's handle */
    public final long window;
    /** The new x coordinate */
    public final int newx;
    /** The new y coordinate */
    public final int newy;

    /**
     * Constructs the event from the data given by GLFW
     *
     * @param window The related window's handle
     * @param newx The new x coordinate
     * @param newy The new y coordinate
     */
    public WindowPosEvent(long window, int newx, int newy){
        this.window = window;
        this.newx = newx;
        this.newy = newy;
    }
}
