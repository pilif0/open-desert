package net.pilif0.open_desert.components;

import net.pilif0.open_desert.ecs.Component;
import net.pilif0.open_desert.ecs.GameObject;
import net.pilif0.open_desert.ecs.GameObjectEvent;
import net.pilif0.open_desert.ecs.Template;

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
    public static final float DEFAULT_ROTATION = 0f;

    /** Rotation amount (degrees) */
    private float rotation = DEFAULT_ROTATION;
    /** Component's owner */
    private GameObject owner;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void handle(GameObjectEvent e) {}

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
        Object val = overrides.getOrDefault("rotation", null);
        if(val instanceof Number) {
            // When the value is a number
            rotation = ((Number) val).floatValue();
        }else if(val instanceof String){
            // When the value is anything else or nothing
            rotation = Float.parseFloat((String) val);
        }
    }

    @Override
    public Object toYaml(Template t) {
        // Retrieve the template default
        Template.ComponentInfo info = t.getComponents().stream()
                .filter(i -> NAME.equals(i.name))
                .findFirst()
                .orElse(null);

        // Check for equal to template values
        if(info != null){
            // Compare the current values to the overrides
            Object val = info.fieldOverrides.getOrDefault("rotation", null);
            if( (val instanceof Number && rotation == ((Number) val).floatValue()) ||
                    (val instanceof String && rotation == Float.parseFloat((String) val)) ||
                    (val == null && rotation == DEFAULT_ROTATION)){
                // Equal to override
                return null;
            }
        }

        // Check for equal to default value
        if(rotation == DEFAULT_ROTATION){
            // Only declare the component
            return NAME;
        }

        // Build full object
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("rotation", rotation);
        result.put(NAME, data);
        return result;
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
     * Add to rotation
     *
     * @param difference Value to add
     */
    public void addRotation(float difference){
        rotation = (rotation + difference) % 360;
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
