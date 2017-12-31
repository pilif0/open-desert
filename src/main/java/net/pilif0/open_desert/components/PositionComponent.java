package net.pilif0.open_desert.components;

import net.pilif0.open_desert.ecs.Component;
import net.pilif0.open_desert.ecs.GameObjectEvent;
import net.pilif0.open_desert.ecs.GameObject;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.HashMap;
import java.util.Map;

/**
 * Component that only contains position and related operations.
 * This is the most basic component guaranteed to be present in all game objects.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class PositionComponent implements Component {
    /** Name of this component */
    public static final String NAME = "position";
    /** Default value for position */
    public static final String DEFAULT_POSITION = "0,0";

    /** Position value */
    private Vector2f position;
    /** Component's owner */
    private GameObject owner;

    /**
     * Construct the component with position (0,0)
     */
    public PositionComponent(){
        position = new Vector2f();
    }

    /**
     * Construct the component with the provided position
     *
     * @param position Position
     */
    public PositionComponent(Vector2fc position){
        this.position = new Vector2f(position);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void handle(GameObjectEvent e) {
        // So far there are no events for this component to handle
    }

    @Override
    public Map<String, Object> getState() {
        Map<String, Object> result = new HashMap<>();
        result.put("position", position);
        return result;
    }

    @Override
    public void onAttach(GameObject owner) {
        // Remember who the component is attached to and set to recalculate
        this.owner = owner;
    }

    @Override
    public void onDetach(GameObject owner) {
        // Forget the owner
        this.owner = null;
    }

    @Override
    public void overrideFields(Map<String, Object> overrides) {
        // Position is serialised as a String with two float values separated by ','
        String val = (String) overrides.getOrDefault("position", DEFAULT_POSITION);
        String[] p = val.split(",");
        position.set(Float.parseFloat(p[0]), Float.parseFloat(p[1]));
    }

    /**
     * Get the position value
     *
     * @return Position value
     */
    public Vector2fc getPosition(){
        return position;
    }

    /**
     * Get the position value in 3D
     *
     * @return Position value
     */
    public Vector3fc getPosition3D(){
        return (new Vector3f(position, 0)).toImmutable();
    }

    /**
     * Set the position
     *
     * @param newValue New position
     */
    public void setPosition(Vector2fc newValue){
        position.set(newValue);
        owner.distributeEvent(new PositionEvent(this));
    }

    /**
     * Add to the position
     *
     * @param difference Value to add
     */
    public void addPosition(Vector2fc difference){
        position.add(difference);
    }

    /**
     * Represents an event in the position, such as a change
     */
    public static class PositionEvent implements GameObjectEvent {
        /** Origin of the event */
        private PositionComponent origin;

        /**
         * Construct the event from its origin
         *
         * @param origin Origin of the event
         */
        public PositionEvent(PositionComponent origin) {
            this.origin = origin;
        }

        @Override
        public Component getOrigin() {
            return origin;
        }
    }
}
