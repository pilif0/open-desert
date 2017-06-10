package net.pilif0.open_desert;

import javafx.util.Pair;
import net.pilif0.open_desert.graphics.ShaderProgram;
import net.pilif0.open_desert.graphics.shapes.Shapes;
import net.pilif0.open_desert.input.Action;
import net.pilif0.open_desert.state.GameState;
import net.pilif0.open_desert.state.StateMachine;
import net.pilif0.open_desert.util.Delta;
import net.pilif0.open_desert.window.Window;
import org.lwjgl.opengl.GL;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Represents the game, connecting all the components together.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Game {
    /** The instance */
    private static Game instance;

    /** The main window */
    private Window window;
    /** The game-state state machine */
    private StateMachine<GameState> states;
    /** The delta time */
    public Delta delta;

    /**
     * Constructs the game instnace
     */
    private Game(){}

    /**
     * Returns the instance
     *
     * @return The instance
     */
    public static Game getInstance(){
        if(instance == null){
            instance = new Game();
        }

        return instance;
    }

    /**
     * Runs the game
     */
    public void run(){
        init();
        loop();
        cleanUp();
    }

    /**
     * Initialises the game
     */
    private void init(){
        //Build and show the window
        window = new Window("Test Window", 1280, 720, Window.Type.WINDOWED, -1, false);
        window.makeContextCurrent();
        window.centre();
        window.inputManager.getKeyCallback().register(e -> {
            //Quit on escape
            if(e.key == GLFW_KEY_ESCAPE && e.action == Action.PRESS) {
                glfwSetWindowShouldClose(window.handle, true);
            }

            //Windowed on F1
            if(e.key == GLFW_KEY_F1 && e.action == Action.PRESS){
                window.makeWindowed(1280, 720);
            }

            //Fullscreen on F2
            if(e.key == GLFW_KEY_F2 && e.action == Action.PRESS){
                window.makeFullscreen(glfwGetPrimaryMonitor());
            }

            //Borderless on F3
            if(e.key == GLFW_KEY_F3 && e.action == Action.PRESS){
                window.makeBorderless(glfwGetPrimaryMonitor());
            }
        });
        window.show();

        //Prepare OpenGL
        GL.createCapabilities();
        glClearColor(0.0f, 0.0f,0.0f,1.0f);
        //glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_NEVER);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        //Prepare the states machine
        LinkedHashSet<GameState> stateSet = new LinkedHashSet<>();
        GameState introState = new IntroState();
        stateSet.add(introState);
        Map<Pair<GameState, Integer>, GameState> transitions = new HashMap<>();
        states = new StateMachine<>(
                stateSet,
                introState,
                transitions
        );

        //Prepare the delta class
        delta = new Delta();

        //Log OpenGL errors
        Launcher.getLog().logOpenGLError("OpenGL", "in game initialisation");
    }

    /**
     * Runs the main loop
     */
    private void loop(){
        //Start the timer and the loop
        delta.start();
        while(!window.isCloseRequested()){
            //Clear the buffer and poll for input
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glfwPollEvents();

            //Update and render the game
            update();
            render();

            //Log OpenGL errors
            Launcher.getLog().logOpenGLError("OpenGL", "in main loop");

            //Swap the buffers
            glfwSwapBuffers(window.handle);

            //Update the timer
            delta.update();
        }
    }

    /**
     * Updates the game
     */
    private void update(){
        states.getCurrent().update();
    }

    /**
     * Renders the game
     */
    private void render(){
        states.getCurrent().render();
    }

    /**
     * Cleans up
     */
    private void cleanUp(){
        //Clean up all the global shaders
        ShaderProgram.DYNAMIC_COLOR_SHADER.cleanUp();
        ShaderProgram.STATIC_COLOR_SHADER.cleanUp();
        ShaderProgram.BASIC_SHADER.cleanUp();
        ShaderProgram.TEXTURE_SHADER.cleanUp();

        //Clean up shapes
        Shapes.cleanUp();

        states.cleanUp();
        window.destroy();
        glfwTerminate();
    }

    /**
     * Returns the main window
     *
     * @return The main window
     */
    public Window getWindow(){ return window; }
}
