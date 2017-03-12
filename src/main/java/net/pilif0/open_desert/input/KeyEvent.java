package net.pilif0.open_desert.input;

import net.pilif0.open_desert.events.Event;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Represents a single key event.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class KeyEvent implements Event {
    /** The handle of the associated window */
    public final long window;
    /** Key code */
    public final int key;
    /** The system specific scan-code of the key */
    public final int scancode;
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
     * @param key The key code
     * @param scancode The system specific scan-code
     * @param action The action performed ({@code PRESS}, {@code RELEASE}, {@code REPEAT})
     * @param mods The bitfield describing which modifier keys were held down
     */
    public KeyEvent(long window, int key, int scancode, int action, int mods){
        this.window = window;
        this.key = key;
        this.scancode = scancode;

        //Select the right action
        switch(action){
            case GLFW_PRESS: this.action = Action.PRESS; break;
            case GLFW_RELEASE: this.action = Action.RELEASE; break;
            case GLFW_REPEAT: this.action = Action.REPEAT; break;
            default: throw new IllegalArgumentException("The action does not represent any of PRESS, RELEASE, REPEAT.");
        }

        //Determine modifier states
        this.shiftDown = (mods & GLFW_MOD_SHIFT) > 0;
        this.controlDown = (mods & GLFW_MOD_CONTROL) > 0;
        this.altDown = (mods & GLFW_MOD_ALT) > 0;
        this.superDown = (mods & GLFW_MOD_SUPER) > 0;
    }
}
