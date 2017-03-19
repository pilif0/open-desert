package net.pilif0.open_desert.util;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.APIUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;

/**
 * Handles logging
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Log{
    /** The datetime format to use for file name and message timestamp */
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
    /** The name of the directory to save the logs in */
    public static final String DIRECTORY = "log";

    /** Path to the log file */
    public final Path path;

    /**
     * Constructs the log from its name.
     * The name is appended with underscore and then the timestamp of the creation datetime.
     *
     * @param name The name to use for the file
     */
    public Log(String name){
        //Note the datetime
        LocalDateTime creationDT = LocalDateTime.now();

        //Create the path
        StringBuilder filename = new StringBuilder();
        if(name != null && !name.equals("")){
            filename.append(name).append("_");
        }
        filename.append(creationDT.format(DATE_TIME_FORMAT))
            .append(".log");
        path = Paths.get(DIRECTORY, filename.toString());

        //Create the file
        try{
            path.toFile().getParentFile().mkdirs();
            path.toFile().createNewFile();
        }catch(IOException e){
            //Cannot create log file -> critical failure, exit
            e.printStackTrace();
            System.err.println("Critical failure: cannot create log file at \'"+path.toAbsolutePath().toString()+"\'");
            System.err.println("Exiting ...");
            System.exit(0);
        }
    }

    /**
     * Constructs the log with no name.
     * The file will be named with the timestamp of the creation datetime.
     */
    public Log(){
        this(null);
    }

    /**
     * Writes the text to the file
     *
     * @param text The text to write
     */
    private synchronized void write(String text){
        try {
            Files.write(path, text.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            //There was an error when logging (Where is your god now?)
            // -> display an error and the text in console
            System.out.printf(
                    "There was an error when trying to write a message to the log at \'%s\'.",
                    path.toAbsolutePath().toString()
            );
            e.printStackTrace();
            System.out.printf("The unwritten message %s\n", text);
        }
    }

    /**
     * Logs the message
     *
     * @param severity The severity of the message
     * @param origin The origin of the message
     * @param message The message
     */
    public synchronized void log(Severity severity, String origin, String message){
        //Note the datetime
        LocalDateTime dt = LocalDateTime.now();

        //Build text
        StringBuilder text = new StringBuilder(dt.format(DATE_TIME_FORMAT));
        text.append(" ").append(severity)
                .append(" ").append(origin)
                .append(" - ").append(message)
                .append(System.lineSeparator());

        //Write the text
        write(text.toString());
    }

    /**
     * Logs the exception including its stack trace
     *
     * @param origin The origin of the exception
     * @param e The exception
     */
    public synchronized void log(String origin, Exception e){
        StringBuilder message = new StringBuilder(e.toString());
        message.append(System.lineSeparator());

        //Append the stack trace
        StackTraceElement[] trace = e.getStackTrace();
        for(int i = 0; i < trace.length; i++){
            message.append("\t\t")
                    .append(trace[i].toString())
                    .append(System.lineSeparator());
        }

        //Log the message
        log(Severity.EXCEPTION, origin, message.toString());
    }

    /**
     * Logs any OpenGL error that occurred before calling this method
     *
     * @param origin The origin of the call
     * @param identifier The identifier of the call (added to the end of the mssage to better describe it)
     *                   or {@code null} to skip
     */
    public synchronized void logOpenGLError(String origin, String identifier){
        for(int glError = glGetError(); glError != GL_NO_ERROR; glError = glGetError()){
            //Build the message
            StringBuilder message = new StringBuilder("OpenGL error #");
            message.append(glError);

            if(identifier != null){
                message.append(" (").append(identifier).append(')');
            }

            //Log the message
            log(Severity.ERROR, origin, message.toString());
        }
    }

    /**
     * Logs the exception including its stack trace while using "Java" as the origin
     *
     * @param e The exception
     */
    public synchronized void log(Exception e){
        log("Java", e);
    }

    /**
     * Subclass of {@code ByteArrayOutputStream} customized to write into a Log
     */
    public static class LogCallback extends GLFWErrorCallback {
        /** The target Log instance */
        private Log target;
        /** The GLFW error codes and messages */
        private Map<Integer, String> ERROR_CODES = APIUtil.apiClassTokens(
                (field, value) -> 0x10000 < value && value < 0x20000,
                null,
                GLFW.class
            );

        /**
         * Constructs the log callback from the target log instance
         *
         * @param target The target log instance
         */
        public LogCallback(Log target){
            this.target = target;
        }

        @Override
        public void invoke(int error, long description) {
            String msg = getDescription(description);

            //Construct the message
            StringBuilder message = new StringBuilder();
            message.append(ERROR_CODES.get(error)).append(" error\n")
                .append("\tDescription : " + msg)
                .append("\tStacktrace  :");

            //Append the stack trace
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            for ( int i = 4; i < stack.length; i++ ) {
                message.append("\n\t\t")
                    .append(stack[i].toString());
            }

            //Log the result
            target.log(Severity.ERROR, "GLFW", message.toString());
        }
    }
}
