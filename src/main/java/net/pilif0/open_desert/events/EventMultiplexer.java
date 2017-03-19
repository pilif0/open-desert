package net.pilif0.open_desert.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An event multiplexer that distributes an event to a list of listeners.
 * If the event is ever consumed, the distribution stops.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class EventMultiplexer<T extends Event> implements EventListener<T> {
    /** The listeners to distribute to */
    private List<EventListener<T>> listeners = new ArrayList<>();

    @Override
    public void handle(T event) {
        //Distribute the event to every listener (sequentially)
        listeners.forEach(l -> l.handle(event));
    }

    /**
     * Adds the listener to the multiplexer list
     *
     * @param l The listener to add
     */
    public void register(EventListener<T> l){
        listeners.add(l);
    }

    /**
     * Adds the listeners to the multiplexer list
     *
     * @param ls The listeners to add
     */
    public void register(EventListener<T>... ls){
        listeners.addAll(Arrays.asList(ls));
    }

    /**
     * Removes the listener from the multiplexer list
     *
     * @param l The listener to remove
     */
    public void remove(EventListener<T> l){
        listeners.remove(l);
    }

    /**
     * Removes the listeners from the multiplexer list
     *
     * @param ls The listeners to remove
     */
    public void remove(EventListener<T>... ls){
        listeners.removeAll(Arrays.asList(ls));
    }
}
