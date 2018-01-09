package net.pilif0.open_desert.input;

import net.pilif0.open_desert.events.Event;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SUPER;

/**
 * Represents a single mouse button event
 *
 * @author Filip Smola
 * @version 1.0
 */
public class MouseButtonEvent implements Event {
    /** The handle of the associated window */
    public final long window;
    /** The button number */
    public final int button;
    /** The action performed */
    public final Action action;
    /** Whether shift was held down */
    public final boolean shiftDown;
    /** Whether control was held down */
    public final boolean controlDown;
    /** Whether alt was held down */
    public final boolean altDown;
    /** Whether super was held down */
    public final boolean superDown;

    /**
     * Constructs the event from the data given by GLFW
     *
     * @param window The handle of the associated window
     * @param button The key code
     * @param action The action performed ({@code PRESS}, {@code RELEASE}, {@code REPEAT})
     * @param mods The bitfield describing which modifier keys were held down
     */
    public MouseButtonEvent(long window, int button, int action, int mods){
        this.window = window;
        this.button = button;

        //Select the right action
        switch (action){
            case GLFW_PRESS: this.action = Action.PRESS; break;
            case GLFW_RELEASE: this.action = Action.RELEASE; break;
            default: throw new IllegalArgumentException("The action does not represent any of PRESS, RELEASE.");
        }

        //Determine modifier states
        this.shiftDown = (mods & GLFW_MOD_SHIFT) > 0;
        this.controlDown = (mods & GLFW_MOD_CONTROL) > 0;
        this.altDown = (mods & GLFW_MOD_ALT) > 0;
        this.superDown = (mods & GLFW_MOD_SUPER) > 0;
    }

    /**
     * Constructs the event from the data of another event (copy)
     *
     * @param source Source event
     */
    public MouseButtonEvent(MouseButtonEvent source){
        this.window = source.window;
        this.button = source.button;
        this.action = source.action;
        this.shiftDown = source.shiftDown;
        this.controlDown = source.controlDown;
        this.altDown = source.altDown;
        this.superDown = source.superDown;
    }
}
