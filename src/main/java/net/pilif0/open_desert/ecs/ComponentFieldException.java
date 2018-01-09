package net.pilif0.open_desert.ecs;

/**
 * Exception for when a there is problem with a component field when instantiating the component.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class ComponentFieldException extends RuntimeException {
    public ComponentFieldException(String message) {
        super(message);
    }

    public ComponentFieldException(String message, Throwable cause) {
        super(message, cause);
    }
}
