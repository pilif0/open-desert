package net.pilif0.open_desert.state;

import net.pilif0.open_desert.events.Event;
import net.pilif0.open_desert.events.EventMultiplexer;
import net.pilif0.open_desert.graphics.Camera;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a game state (intro, main menu, options, ...)
 *
 * @author Filip Smola
 * @version 1.0
 */
public abstract class GameState {
    /** Map of all states indexed by their IDs */
    private static Map<Integer, GameState> states = new HashMap<>();
    /** The last assigned ID */
    private static int lastID = 0;

    /** The event multiplexer for the state events */
    public final EventMultiplexer<GameStateEvent> eventMultiplexer = new EventMultiplexer<>();
    /** The unique ID of this state */
    public final int ID;

    /**
     * Constructs the state assigning it an ID, initialising the transitions map and adding it to the states map
     */
    public GameState(){
        ID = ++lastID;
        states.put(ID, this);
    }

    /**
     * Returns the name of the state
     *
     * @return The name of the state
     */
    public abstract String getName();

    /**
     * Enters the state (fires an event)
     */
    public void enter(){
        onEnter();
        eventMultiplexer.handle(new GameStateEvent(GameStateEvent.Type.ENTER));
    }

    /**
     * Called when entering the state, before firing the event
     */
    protected abstract void onEnter();

    /**
     * Updates the state (fires an event)
     */
    public void update(){
        onUpdate();
        eventMultiplexer.handle(new GameStateEvent(GameStateEvent.Type.UPDATE));
    }

    /**
     * Called when updating the state, before firing the event
     */
    protected abstract void onUpdate();

    /**
     * Renders the state (fires an event)
     */
    public void render(){
        onRender();
        eventMultiplexer.handle(new GameStateEvent(GameStateEvent.Type.RENDER));
    }

    /**
     * Called when rendering the state, before firing the event
     */
    protected abstract void onRender();

    /**
     * Exits the state (fires an event)
     */
    public void exit(){
        onExit();
        eventMultiplexer.handle(new GameStateEvent(GameStateEvent.Type.EXIT));
    }

    /**
     * Called when exiting the state, before firing the event
     */
    public abstract void onExit();

    /**
     * Cleans up the state (fires an event)
     */
    public void cleanUp(){
        onCleanUp();
        eventMultiplexer.handle(new GameStateEvent(GameStateEvent.Type.CLEANUP));
    }

    /**
     * Called when entering the state, before firing the event
     */
    public abstract void onCleanUp();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameState)) return false;

        GameState state = (GameState) o;

        return ID == state.ID;
    }

    @Override
    public int hashCode() {
        return ID;
    }

    /**
     * Return main state camera
     *
     * @return Main camera
     */
    public abstract Camera getCamera();

    /**
     * Returns the state with the ID provided
     *
     * @param id The ID to look for
     * @return The state with the ID or {@code null} if none present
     */
    public static GameState getState(int id){
        return states.get(id);
    }

    /**
     * An event for the game state
     */
    public static class GameStateEvent implements Event{
        /** The possible types of the event */
        enum Type {ENTER, UPDATE, RENDER, EXIT, CLEANUP}

        /** The event type */
        public final Type type;

        /**
         * Constructs the event from its type
         *
         * @param type The event type
         */
        public GameStateEvent(Type type){
            this.type = type;
        }
    }
}
