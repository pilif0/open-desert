package net.pilif0.open_desert.components;

import net.pilif0.open_desert.Game;
import net.pilif0.open_desert.ecs.Component;
import net.pilif0.open_desert.ecs.GameObject;
import net.pilif0.open_desert.ecs.GameObjectEvent;
import net.pilif0.open_desert.events.EventListener;
import net.pilif0.open_desert.input.ScrollEvent;

import java.util.Map;

/**
 * Component that delegates scroll events through the game object's components.
 * It has no fields, just behaviour.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class ScrollSensitiveComponent implements Component, EventListener<ScrollEvent> {
    /** Name of this component */
    public static final String NAME = "scroll_sensitive";

    /** Component's owner */
    private GameObject owner;

    /**
     * Construct the component
     */
    public ScrollSensitiveComponent(){}

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

        // Attach self to mouse button input
        Game.getInstance().getWindow().inputManager.getScrollCallback().register(this);
    }

    @Override
    public void onDetach(GameObject owner) {
        // Forget the owner
        this.owner = null;

        // Detach self from mouse button input
        Game.getInstance().getWindow().inputManager.getScrollCallback().remove(this);
    }

    @Override
    public void overrideFields(Map<String, Object> overrides) {
        // There is nothing to override
    }

    /**
     * Represents an event from the keyboard. Contains all the relevant data.
     */
    public static class ScrollEvent extends net.pilif0.open_desert.input.ScrollEvent implements GameObjectEvent {
        /** Origin of the event */
        private ScrollSensitiveComponent origin;

        /**
         * Construct the event from its origin
         *
         * @param origin Origin of the event
         */
        public ScrollEvent(ScrollSensitiveComponent origin, net.pilif0.open_desert.input.ScrollEvent event) {
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
    public void handle(net.pilif0.open_desert.input.ScrollEvent event) {
        owner.distributeEvent(new ScrollEvent(this, event));
    }
}
