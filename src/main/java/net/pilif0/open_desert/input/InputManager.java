package net.pilif0.open_desert.input;

import net.pilif0.open_desert.events.EventMultiplexer;
import net.pilif0.open_desert.window.Window;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import java.lang.ref.WeakReference;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

/**
 * Manages the input of a window
 *
 * @author Filip Smola
 * @version 1.0
 */
public class InputManager {
    /** The key event multiplexer */
    private final KeyCallback keyCallback;
    /** A reference to the window this manager is attached to */
    private final WeakReference<Window> window;

    /**
     * Constructs the input manager
     *
     * @param w The window this manager is attached to
     */
    public InputManager(Window w){
        window = new WeakReference<>(w);
        keyCallback = new KeyCallback();
    }

    /**
     * Returns whether the key is pressed
     *
     * @param keyCode The GLFW key code
     * @return Whether the key is pressed
     */
    public boolean isKeyDown(int keyCode){
        try {
            return glfwGetKey(window.get().handle, keyCode) == GLFW_PRESS;
        }catch(NullPointerException e){
            //If the window is null, the key is not pressed
            return false;
        }
    }

    /**
     * Returns the GLFW key callback
     *
     * @return The GLFW key callback
     */
    public GLFWKeyCallbackI getKeyCallback(){ return keyCallback; }

    /**
     * Returns the key event multiplexer
     *
     * @return The event multiplexer
     */
    public EventMultiplexer<KeyEvent> getEventMultiplexer(){ return keyCallback; }

    /**
     * The window key callback.
     * Bridges the GLFW callback system with the event system.
     */
    public static class KeyCallback extends EventMultiplexer<KeyEvent> implements GLFWKeyCallbackI {

        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            this.handle(new KeyEvent(window, key, scancode, action, mods));
        }
    }
}
