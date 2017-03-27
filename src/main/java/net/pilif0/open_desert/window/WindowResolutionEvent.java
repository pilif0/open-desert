package net.pilif0.open_desert.window;

import net.pilif0.open_desert.events.Event;

/**
 * Represents a window resolution change event
 *
 * @author Filip Smola
 * @version 1.0
 */
public class WindowResolutionEvent implements Event {
    /** The related window's handle */
    public final long window;
    /** The new horizontal resolution */
    public final int newX;
    /** The new vertical resolution */
    public final int newY;

    /**
     * Constructs the event from the data given by the window
     *
     * @param window The related window's handle
     * @param newX The new horizontal resolution
     * @param newY The new vertical resolution
     */
    public WindowResolutionEvent(long window, int newX, int newY){
        this.window = window;
        this.newX = newX;
        this.newY = newY;
    }
}
