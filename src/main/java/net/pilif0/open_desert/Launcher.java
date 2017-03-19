package net.pilif0.open_desert;

import net.pilif0.open_desert.util.Log;
import net.pilif0.open_desert.util.Severity;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.Configuration;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;

/**
 * This class is the game entry point.
 * It takes care of setting up the environment and launching the game.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Launcher {
    /** The main log */
    private static Log log = new Log();
    /** The game instance */
    private static Game game;

    /**
     * Sets up the environment and launches the game
     * If debug is present in the command line arguments, the debug mode will be turned on
     *
     * @param args The command line arguments
     */
    public static void main(String[] args){
        //Decide whether to turn debug on
        boolean debugPresent = Arrays.stream(args)
                .filter(x -> x.equals("debug"))
                .count() > 0;

        //Announce start into the log
        log.log(Severity.INFO, "Launcher", "Launching ...");

        //Set the path to the natives
        Configuration.LIBRARY_PATH.set("natives/");

        //Create and set the GLFW error callback
        (new Log.LogCallback(log)).set();

        //Instantiate and run the game
        game = new Game(debugPresent);
        game.run();

        //Announce end into the log
        log.log(Severity.INFO, "Launcher", "Game finished ...");

        //Free GLFW error callback
        GLFWErrorCallback callback = glfwSetErrorCallback(null);
        if(callback != null){
            callback.free();
        }

        //Announce successful cleanup into the log
        log.log(Severity.INFO, "Launcher", "Cleaned up, exiting ...");
    }

    /**
     * Returns the main log
     *
     * @return The main log
     */
    public static Log getLog(){ return log; }

    /**
     * Returns the game instance
     *
     * @return The game instance
     */
    public static Game getGame(){ return game; }
}
