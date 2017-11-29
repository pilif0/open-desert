package net.pilif0.open_desert.components;

import net.pilif0.open_desert.ecs.Component;
import net.pilif0.open_desert.ecs.GameObject;
import net.pilif0.open_desert.ecs.GameObjectEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Component that only contains rotation and related operations.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class RotationComponent implements Component{
    /** Name of this component */
    public static final String NAME = "rotation";
    /** Default value for rotation */
    public static final String DEFAULT_ROTATION = "0";

    /** Rotation amount (degrees) */
    private float rotation;
    /** Component's owner */
    private GameObject owner;

    /**
     * Construct the component with rotation of 0 degrees
     */
    public RotationComponent(){
        rotation = 0;
    }

    /**
     * Construct the component with the provided rotation
     *
     * @param rotation Rotation
     */
    public RotationComponent(float rotation){
        this.rotation = rotation;
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
        result.put("rotation", rotation);
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
        // Rotation is serialised as a String with one float value
        String val = (String) overrides.getOrDefault("rotation", DEFAULT_ROTATION);
        rotation = Float.parseFloat(val);
    }

    /**
     * Get the rotation value
     *
     * @return Rotation value
     */
    public float getRotation(){
        return rotation;
    }

    /**
     * Set the rotation
     *
     * @param newValue New rotation
     */
    public void setRotation(float newValue){
        rotation = newValue;
        owner.distributeEvent(new RotationEvent(this));
    }

    /**
     * Represents an event in the rotation, such as a change
     */
    public static class RotationEvent implements GameObjectEvent {
        /** Origin of the event */
        private RotationComponent origin;

        /**
         * Construct the event from its origin
         *
         * @param origin Origin of the event
         */
        public RotationEvent(RotationComponent origin) {
            this.origin = origin;
        }

        @Override
        public Component getOrigin() {
            return origin;
        }
    }
}
