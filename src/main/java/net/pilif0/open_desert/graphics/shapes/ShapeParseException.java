package net.pilif0.open_desert.graphics.shapes;

import java.nio.file.Path;

/**
 * Exception to represent errors while parsing shape files
 *
 * @author Filip Smola
 * @version 1.0
 */
public class ShapeParseException extends RuntimeException {

    /**
     * Constructs an exception with a generic message and the path to the file
     *
     * @param p The path to the file that caused the error
     */
    public ShapeParseException(Path p) {
        super("An exception occurred while parsing \'"+p.toAbsolutePath().toString()+"\'");
    }

    /**
     * Constructs an exception with the desired message and the path to the file
     *
     * @param p The path to the file that caused the error
     * @param message The desired message
     */
    public ShapeParseException(Path p, String message) {
        super(message + " when parsing \'"+p.toAbsolutePath().toString()+"\'");
    }

    /**
     * Constructs an exception with a generic message, the path to the file, and the cause
     *
     * @param p The path to the file that caused the error
     * @param e The cause
     */
    public ShapeParseException(Path p, Exception e){
        super("An exception occurred while parsing \'"+p.toAbsolutePath().toString()+"\'", e);
    }
}
