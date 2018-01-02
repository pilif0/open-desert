package net.pilif0.open_desert.components;

import net.pilif0.open_desert.Game;
import net.pilif0.open_desert.ecs.Component;
import net.pilif0.open_desert.ecs.GameObject;
import net.pilif0.open_desert.ecs.GameObjectEvent;
import net.pilif0.open_desert.events.EventListener;
import net.pilif0.open_desert.input.KeyEvent;

import java.util.Map;

/**
 * Component that delegates keyboard events through the game object's components.
 * It has no fields, just behaviour.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class KeyboardSensitiveComponent implements EventListener<KeyEvent>, Component {
    /** Name of this component */
    public static final String NAME = "keyboard_sensitive";

    /** Component's owner */
    private GameObject owner;

    /**
     * Construct the component
     */
    public KeyboardSensitiveComponent(){}

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void handle(GameObjectEvent e) {
        // So far there are no events for this component to handle
    }

    @Override
    public void onAttach(GameObject owner) {
        // Remember who the component is attached to and set to recalculate
        this.owner = owner;

        // Attach self to keyboard input
        Game.getInstance().getWindow().inputManager.getKeyCallback().register(this);
    }

    @Override
    public void onDetach(GameObject owner) {
        // Forget the owner
        this.owner = null;

        // Detach self from keyboard input
        Game.getInstance().getWindow().inputManager.getKeyCallback().remove(this);
    }

    @Override
    public void overrideFields(Map<String, Object> overrides) {
        // There is nothing to override
    }

    /**
     * Represents an event from the keyboard. Contains all the relevant data.
     */
    public static class KeyboardEvent extends KeyEvent implements GameObjectEvent {
        /** Origin of the event */
        private KeyboardSensitiveComponent origin;

        /**
         * Construct the event from its origin
         *
         * @param origin Origin of the event
         */
        public KeyboardEvent(KeyboardSensitiveComponent origin, KeyEvent event) {
            super(event);
            this.origin = origin;
        }

        @Override
        public Component getOrigin() {
            return origin;
        }
    }

    /**
     * Handle the event
     *
     * @param event Event to handle
     */
    @Override
    public void handle(KeyEvent event) {
        owner.distributeEvent(new KeyboardEvent(this, event));
    }
}
