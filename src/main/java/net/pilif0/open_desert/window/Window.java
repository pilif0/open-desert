package net.pilif0.open_desert.window;

import net.pilif0.open_desert.events.EventMultiplexer;
import net.pilif0.open_desert.input.InputManager;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;
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
    /** The input manager */
    public final InputManager inputManager;
    /** The window title */
    private String title;
    /** The top left corner of the window */
    private Vector2f position;
    /** The size of the window */
    private Vector2i size;
    /** The resolution of the window (for windowed equal to size) */
    private Vector2i resolution;
    /** The window type */
    private Type type;
    /** Whether vSync is turned on */
    private final boolean vSync;
    /** The current monitor */
    private long monitorID;

    /** The window size event multiplexer */
    public final WindowSizeMultiplexer sizeMultiplexer;
    /** The window position event multiplexer */
    public final WindowPosMultiplexer posMultiplexer;
    /** The window focus event multiplexer */
    public final WindowFocusMultiplexer focusMultiplexer;
    /** The window close event multiplexer */
    public final WindowCloseMultiplexer closeMultiplexer;
    /** The window resolution event multiplexer */
    public final EventMultiplexer<WindowResolutionEvent> resolutionMultiplexer;

    /**
     * Constructs the window using the parameters provided
     * The window will be windowed and vSync will be off
     *
     * @param title The title to use
     * @param width The width of the window
     * @param height The height of the window
     */
    public Window(String title, int width, int height){
        this(title, width, height, Type.WINDOWED, -1, false);
    }

    /**
     * Constructs the window using the parameters provided
     * The resolution will be equal to the size of the window
     *
     * @param title The title to use
     * @param width The width of the window
     * @param height The height of the window
     * @param type The window type
     * @param monitor The monitor to use for fullscreen or borderless
     * @param vSync Whether vSync should be enabled
     */
    public Window(String title, int width, int height, Type type, int monitor, boolean vSync){
        this(title, width, height, width, height, type, monitor, vSync);
    }


    /**
     * Constructs the window using the parameters provided
     *
     * @param title The title to use
     * @param width The width of the window
     * @param height The height of the window
     * @param resX The horizontal resolution
     * @param resY The vertical resolution
     * @param type The window type
     * @param monitor The monitor to use for fullscreen or borderless
     * @param vSync Whether vSync should be enabled
     */
    public Window(String title, int width, int height, int resX, int resY, Type type, int monitor, boolean vSync){
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
        size = new Vector2i(width, height);
        this.title = title;
        this.vSync = vSync;
        resolution = new Vector2i(resX, resY);

        //Attach input manager
        inputManager = new InputManager(this);
        glfwSetKeyCallback(handle, inputManager.getKeyCallback());

        //Set the window event multiplexers
        sizeMultiplexer = new WindowSizeMultiplexer();
        glfwSetWindowSizeCallback(handle, sizeMultiplexer);
        posMultiplexer = new WindowPosMultiplexer();
        glfwSetWindowPosCallback(handle, posMultiplexer);
        focusMultiplexer = new WindowFocusMultiplexer();
        glfwSetWindowFocusCallback(handle, focusMultiplexer);
        closeMultiplexer = new WindowCloseMultiplexer();
        glfwSetWindowCloseCallback(handle, closeMultiplexer);
        resolutionMultiplexer = new EventMultiplexer<>();

        //Register size change listener to update the viewport
        sizeMultiplexer.register(e -> glViewport(0, 0, e.width, e.height));

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
    public void setPosition(Vector2fc position){
        glfwSetWindowPos(handle, (int) position.x(), (int) position.y());
        this.position.set(position);
    }

    /**
     * Returns the window size
     *
     * @return The window size
     */
    public Vector2ic getSize(){
        return size.toImmutable();
    }

    /**
     * Changes the window size
     *
     * @param size The window size
     */
    public void setSize(Vector2ic size){
        glfwSetWindowSize(handle, size.x(), size.y());
        this.size.set(size);
        sizeMultiplexer.handle(new WindowSizeEvent(handle, size.x(), size.y()));
    }

    /**
     * Returns the window resolution
     *
     * @return The window resolution
     */
    public Vector2ic getResolution(){ return resolution.toImmutable(); }

    /**
     * Changes teh window resolution
     *
     * @param resolution The window resolution
     */
    public void setResolution(Vector2ic resolution){
        this.resolution.set(resolution);
        resolutionMultiplexer.handle(new WindowResolutionEvent(handle, resolution.x(), resolution.y()));
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
        //Window the window, change size and resolution based on params

        //Skip if already windowed
        if(type == Type.WINDOWED){
            return;
        }

        //Remove the monitor
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
        resolution.set(width, height);

        //Fire the resize and resolution events
        sizeMultiplexer.handle(new WindowSizeEvent(handle, width, height));
        resolutionMultiplexer.handle(new WindowResolutionEvent(handle, width, height));
    }

    /**
     * Makes the window borderless, overriding current properties with the native video mode of the monitor
     *
     * @param monitor The monitor where the window should be located
     */
    public void makeBorderless(long monitor){
        //Fullscreen, change the size and resolution based on the video mode

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
        resolution.set(mode.width(), mode.height());

        //Fire the resize and resolution events
        sizeMultiplexer.handle(new WindowSizeEvent(handle, mode.width(), mode.height()));
        resolutionMultiplexer.handle(new WindowResolutionEvent(handle, mode.width(), mode.height()));
    }

    /**
     * Makes the window fullscreen, using the current properties
     *
     * @param monitor The handle of the monitor where the window should be located
     */
    public void makeFullscreen(long monitor){
        //Fullscreen, keep the size and the resolution

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
                size.x,
                size.y,
                mode.refreshRate()
        );
        position.set(0f, 0f);
        type = Type.FULLSCREEN;

        //Fire the resize event
        sizeMultiplexer.handle(new WindowSizeEvent(handle, size.x, size.y));
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
                    size.x,
                    size.y,
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
            int newX = posX.get(0) + (mode.width() / 2) - (size.x / 2);
            int newY = posY.get(0) + (mode.height() / 2) - (size.y / 2);
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
     * The window size callback
     */
    public static class WindowSizeMultiplexer extends EventMultiplexer<WindowSizeEvent>
            implements GLFWWindowSizeCallbackI{

        @Override
        public void invoke(long window, int width, int height) {
            this.handle(new WindowSizeEvent(window, width, height));
        }
    }

    /**
     * The window position callback
     */
    public static class WindowPosMultiplexer extends EventMultiplexer<WindowPosEvent>
            implements GLFWWindowPosCallbackI{

        @Override
        public void invoke(long window, int xpos, int ypos) {
            this.handle(new WindowPosEvent(window, xpos, ypos));
        }
    }

    /**
     * The window focus callback
     */
    public static class WindowFocusMultiplexer extends EventMultiplexer<WindowFocusEvent>
            implements GLFWWindowFocusCallbackI{

        @Override
        public void invoke(long window, boolean focused) {
            this.handle(new WindowFocusEvent(window, focused));
        }
    }

    /**
     * The window close callback
     */
    public static class WindowCloseMultiplexer extends EventMultiplexer<WindowCloseEvent>
            implements GLFWWindowCloseCallbackI{

        @Override
        public void invoke(long window) {
            this.handle(new WindowCloseEvent(window));
        }
    }
}
