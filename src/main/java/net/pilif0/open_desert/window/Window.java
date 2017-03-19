package net.pilif0.open_desert.window;

import com.sun.istack.internal.NotNull;
import net.pilif0.open_desert.events.EventMultiplexer;
import net.pilif0.open_desert.input.KeyEvent;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Represents a window
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Window {
    /** The possible types of windows */
    public enum Type {WINDOWED, BORDERLESS, FULLSCREEN}

    /** Whether the windows should be resizable */
    public static final int RESIZABLE = GLFW_FALSE;
    /** Map of all created windows to their handles*/
    public static Map<Long, Window> windows = new HashMap<>();

    /** The window handle */
    public final long handle;
    /** The input callback */
    public final KeyCallback keyCallback;
    /** The window title */
    private String title;
    /** The top left corner of the window */
    private Vector2f position;
    /** The size of the window */
    private Vector2f size;
    /** The window type */
    private Type type;
    /** Whether vSync is turned on */
    private final boolean vSync;
    /** Whether the window was resized */
    private boolean resized;
    /** The current monitor */
    private long monitorID;

    /**
     * Constructs the window using the parameters provided
     * The window will be windowed and vSync will be off
     *
     * @param title The title to use
     * @param width The width of the window
     * @param height The height of the window
     */
    public Window(@NotNull String title, int width, int height){
        this(title, width, height, Type.WINDOWED, -1, false);
    }

    /**
     * Constructs the window using the parameters provided
     *
     * @param title The title to use
     * @param width The width of the window
     * @param height The height of the window
     * @param type The window type
     * @param monitor The monitor to use for fullscreen or borderless
     * @param vSync Whether vSync should be enabled
     */
    public Window(@NotNull String title, int width, int height, @NotNull Type type, int monitor, boolean vSync){
        //Make sure GLFW is initialised
        if(!glfwInit()){
            throw new IllegalStateException("Could not initialise GLFW");
        }

        //Configure the window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, RESIZABLE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        //Process type and monitor
        this.type = type;
        monitorID = NULL;
        if(type != Type.WINDOWED){
            //Case: not windowed -> monitor needed

            monitorID = glfwGetMonitors().get(monitor);
        }

        //When borderless override settings with native video mode
        if(type == Type.BORDERLESS){
            //Retrieve the video mode
            GLFWVidMode mode = glfwGetVideoMode(monitorID);

            glfwWindowHint(GLFW_RED_BITS, mode.redBits());
            glfwWindowHint(GLFW_GREEN_BITS, mode.greenBits());
            glfwWindowHint(GLFW_BLUE_BITS, mode.blueBits());
            glfwWindowHint(GLFW_REFRESH_RATE, mode.refreshRate());

            width = mode.width();
            height = mode.height();
        }

        //Create the window
        handle = glfwCreateWindow(width, height, title, monitorID, NULL);

        //Verify
        if(handle == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window");
        }

        //Enable vSync if requested
        if(vSync){
            glfwSwapInterval(1);
        }

        //Set members
        try(MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer xpos = stack.mallocInt(1);
            IntBuffer ypos = stack.mallocInt(1);
            glfwGetWindowPos(handle, xpos, ypos);
            position = new Vector2f(xpos.get(0), ypos.get(0));
        }
        size = new Vector2f(width, height);
        this.title = title;
        this.vSync = vSync;

        //Assign key callback
        keyCallback = new KeyCallback();
        glfwSetKeyCallback(handle, keyCallback);

        //Register the window
        windows.put(handle, this);
    }

    /**
     * Returns the title
     *
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Changes the title
     *
     * @param title The new title
     */
    public void setTitle(String title) {
        //Change the actual title and the field
        glfwSetWindowTitle(handle, title);
        this.title = title;
    }

    /**
     * Returns the window position
     *
     * @return The window position
     */
    public Vector2fc getPosition(){
        return position.toImmutable();
    }

    /**
     * Changes the window position
     *
     * @param position The new position
     */
    public void setPosition(@NotNull Vector2fc position){
        glfwSetWindowPos(handle, (int) position.x(), (int) position.y());
        this.position.set(position);
    }

    /**
     * Returns the window size
     *
     * @return The window size
     */
    public Vector2fc getSize(){
        return size.toImmutable();
    }

    /**
     * Changes the window size
     *
     * @param size The window size
     */
    public void setSize(@NotNull Vector2fc size){
        glfwSetWindowSize(handle, (int) size.x(), (int) size.y());
        this.size.set(size);
        this.resized = true;
    }

    /**
     * Returns the window type
     *
     * @return The window type
     */
    public Type getType() {
        return type;
    }

    /**
     * Makes the window windowed with the desired dimensions and centers it
     *
     * @param width The width
     * @param height The height
     */
    public void makeWindowed(int width, int height){
        //Skip if already windowed
        if(type == Type.WINDOWED){
            return;
        }

        //Retrieve the monitor's video mode
        monitorID = NULL;

        //Change the window
        glfwSetWindowMonitor(
                handle,
                NULL,
                0,
                0,
                width,
                height,
                GLFW_DONT_CARE
        );
        position.set(0f, 0f);
        size.set(width, height);
        type = Type.WINDOWED;
        centre();
    }

    /**
     * Makes the window borderless, overriding current properties with the native video mode of the monitor
     *
     * @param monitor The monitor where the window should be located
     */
    public void makeBorderless(long monitor){
        //Skip if already borderless
        if(type == Type.BORDERLESS){
            return;
        }

        //Retrieve the monitor's video mode
        monitorID = monitor;
        GLFWVidMode mode = glfwGetVideoMode(monitorID);

        //Change the window
        glfwSetWindowMonitor(
                handle,
                monitorID,
                0,
                0,
                mode.width(),
                mode.height(),
                mode.refreshRate()
        );
        position.set(0f, 0f);
        size.set(mode.width(), mode.height());
        type = Type.BORDERLESS;
    }

    /**
     * Makes the window fullscreen, using the current properties
     *
     * @param monitor The handle of the monitor where the window should be located
     */
    public void makeFullscreen(long monitor){
        //Skip if already fullscreen
        if(type == Type.FULLSCREEN){
            return;
        }

        //Retrieve the monitor's video mode
        monitorID = monitor;
        GLFWVidMode mode = glfwGetVideoMode(monitorID);

        //Change the window
        glfwSetWindowMonitor(
                handle,
                monitorID,
                0,
                0,
                (int) size.x,
                (int) size.y,
                mode.refreshRate()
        );
        position.set(0f, 0f);
        type = Type.FULLSCREEN;
    }

    /**
     * Moves the window to the monitor (for windowed it centres it on that monitor)
     *
     * @param monitor The handle of the monitor that the window should move to
     */
    public void changeMonitor(long monitor){
        //PROBABLY WRONG --- NEEDS TESTING

        //Simply change monitor for fullscreen and borderless, move for windowed
        if(type == Type.FULLSCREEN){
            //Retrieve the monitor and video mode
            monitorID = monitor;
            GLFWVidMode mode = glfwGetVideoMode(monitorID);

            glfwSetWindowMonitor(
                    handle,
                    monitorID,
                    0,
                    0,
                    (int) size.x,
                    (int) size.y,
                    mode.refreshRate()
            );
        }else if(type == Type.BORDERLESS){
            //Retrieve the monitor and video mode
            monitorID = monitor;
            GLFWVidMode mode = glfwGetVideoMode(monitorID);

            //Change dimensions to fit new video mode
            position.set(0f, 0f);
            size.set(mode.width(), mode.height());

            glfwSetWindowMonitor(
                    handle,
                    monitorID,
                    0,
                    0,
                    mode.width(),
                    mode.height(),
                    mode.refreshRate()
            );
        }else{
            //Retrieve the top left corner of the monitor
            IntBuffer posX;
            IntBuffer posY;
            try(MemoryStack stack = MemoryStack.stackPush()){
                posX = stack.mallocInt(1);
                posY = stack.mallocInt(1);
                glfwGetMonitorPos(monitor, posX, posY);
            }

            //Centre the window on that monitor
            GLFWVidMode mode = glfwGetVideoMode(monitor);
            int newX = posX.get(0) + (mode.width() / 2) - (int) (size.x / 2);
            int newY = posY.get(0) + (mode.height() / 2) - (int) (size.y / 2);
            position.set(newX, newY);

            //Move the window and change the field
            glfwSetWindowPos(handle, newX, newY);
        }
    }

    /**
     * Returns whether vSync is enabled
     *
     * @return Whether vSync is enabled
     */
    public boolean isvSync() {
        return vSync;
    }

    /**
     * Returns whether the window was resized
     *
     * @param consume Whether the flag should be reset after returning
     * @return Whether the window was resized
     */
    public boolean getResized(boolean consume){
        boolean buffer = resized;
        if(consume) resized = false;
        return buffer;
    }

    /**
     * Returns whether this window has requested to close
     *
     * @return Whether the window should close
     */
    public boolean isCloseRequested(){
        return glfwWindowShouldClose(handle);
    }

    /**
     * Destroys the window
     */
    public void destroy(){
        windows.remove(handle);
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);
    }

    /**
     * Hides the window
     */
    public void hide(){
        glfwHideWindow(handle);
    }

    /**
     * Shows the window
     */
    public void show(){
        glfwShowWindow(handle);
    }

    /**
     * Makes the OpenGL context current
     */
    public void makeContextCurrent(){
        glfwMakeContextCurrent(handle);
    }

    /**
     * Centres a window on the primary monitor (needs to be windowed)
     */
    public void centre(){
        //Skip if not windowed
        if(type != Type.WINDOWED){
            return;
        }

        //Retrieve the handle of the primary monitor
        long primaryMonitor = glfwGetPrimaryMonitor();

        //Retrieve the top left corner of the primary monitor
        IntBuffer posX;
        IntBuffer posY;
        try(MemoryStack stack = MemoryStack.stackPush()){
            posX = stack.mallocInt(1);
            posY = stack.mallocInt(1);
            glfwGetMonitorPos(primaryMonitor, posX, posY);
        }

        //Centre the window on that monitor
        GLFWVidMode mode = glfwGetVideoMode(primaryMonitor);
        int newX = posX.get(0) + (mode.width() / 2) - (int) (size.x / 2);
        int newY = posY.get(0) + (mode.height() / 2) - (int) (size.y / 2);

        //Move the window and change the field
        glfwSetWindowPos(handle, newX, newY);
        position.set(newX, newY);
    }

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
