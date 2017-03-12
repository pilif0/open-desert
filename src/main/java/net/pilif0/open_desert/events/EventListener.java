package net.pilif0.open_desert.events;

import com.sun.istack.internal.NotNull;

/**
 * An interface for event listeners
 *
 * @author Filip Smola
 * @version 1.0
 */
public interface EventListener<T extends Event> {
    /**
     * Handles the event
     *
     * @param event The event to handle
     */
    void handle(@NotNull final T event);
}
