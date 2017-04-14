package net.pilif0.open_desert.input;

import net.pilif0.open_desert.events.EventMultiplexer;
import net.pilif0.open_desert.window.Window;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.lwjgl.system.MemoryStack;

import java.lang.ref.WeakReference;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Manages the input of a window
 *
 * @author Filip Smola
 * @version 1.0
 */
public class InputManager {
    /** The key event multiplexer */
    private final KeyCallback keyCallback;
    /** The mouse button event multiplexer */
    private final MouseButtonCallback mouseButtonCallback;
    /** The scroll event multiplexer */
    private final ScrollCallback scrollCallback;
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
        mouseButtonCallback = new MouseButtonCallback();
        scrollCallback = new ScrollCallback();

        //Assign the callbacks
        glfwSetKeyCallback(w.handle, keyCallback);
        glfwSetMouseButtonCallback(w.handle, mouseButtonCallback);
        glfwSetScrollCallback(w.handle, scrollCallback);
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
     * Returns whether the mouse button is pressed
     *
     * @param button The button number
     * @return Whether the button is pressed
     */
    public boolean isMouseButtonDown(int button){
        try {
            return glfwGetMouseButton(window.get().handle, button) == GLFW_PRESS;
        }catch(NullPointerException e){
            //If the window is null, the button is not pressed
            return false;
        }
    }

    /**
     * Returns the mouse position
     *
     * @return The mouse position
     */
    public Vector2fc getMousePosition(){
        Vector2f result = new Vector2f();

        try(MemoryStack stack = MemoryStack.stackPush()){
            DoubleBuffer x = stack.mallocDouble(1);
            DoubleBuffer y = stack.mallocDouble(1);

            try{
                glfwGetCursorPos(window.get().handle, x, y);
            }catch(NullPointerException e){
                //If the window is null, the cursor is at (0,0)
                x.put(0);
                y.put(0);
            }

            result.set((float) x.get(), (float) y.get());
        }

        return result;
    }

    /**
     * Returns the key callback
     *
     * @return The key callback
     */
    public KeyCallback getKeyCallback(){ return keyCallback; }

    /**
     * Returns the mouse button callback
     *
     * @return The mouse button callback
     */
    public MouseButtonCallback getMouseButtonCallback(){ return mouseButtonCallback; }

    /**
     * Returns the scroll callback
     *
     * @return The scroll callback
     */
    public ScrollCallback getScrollCallback(){ return scrollCallback; }

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

    /**
     * The window mouse button callback.
     * Bridges the GLFW callback system with the event system.
     */
    public static class MouseButtonCallback extends EventMultiplexer<MouseButtonEvent> implements GLFWMouseButtonCallbackI {

        @Override
        public void invoke(long window, int button, int action, int mods) {
            this.handle(new MouseButtonEvent(window, button, action, mods));
        }
    }

    /**
     * The window scroll callback.
     * Bridges the GLFW callback system with the event system.
     */
    public static class ScrollCallback extends EventMultiplexer<ScrollEvent> implements GLFWScrollCallbackI {

        @Override
        public void invoke(long window, double xoffset, double yoffset) {
            this.handle(new ScrollEvent(window, xoffset, yoffset));
        }
    }
}
