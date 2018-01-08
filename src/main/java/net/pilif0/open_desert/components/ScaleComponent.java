package net.pilif0.open_desert.components;

import net.pilif0.open_desert.ecs.Component;
import net.pilif0.open_desert.ecs.GameObject;
import net.pilif0.open_desert.ecs.GameObjectEvent;
import net.pilif0.open_desert.ecs.Template;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.HashMap;
import java.util.Map;

/**
 * Component that only contains scale and related operations.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class ScaleComponent implements Component {
    /** Name of this component */
    public static final String NAME = "scale";
    /** Default value for scale */
    public static final Vector2fc DEFAULT_SCALE = new Vector2f(1, 1);

    /** Scale factors */
    private Vector2f scale = new Vector2f(DEFAULT_SCALE);
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
        // Scale is serialised as a String with two float values separated by ','
        Object val = overrides.getOrDefault("scale", null);
        if(val instanceof String){
            String[] s = ((String) val).split(",");
            scale.set(Float.parseFloat(s[0]), Float.parseFloat(s[1]));
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
            Object val = info.fieldOverrides.getOrDefault("scale", null);
            if(val != null && val instanceof String){
                String[] s = ((String) val).split(",");
                if(scale.x() == Float.parseFloat(s[0]) && scale.y() == Float.parseFloat(s[1])){
                    // Equal to the override
                    return null;
                }
            }else if(val == null && scale.equals(DEFAULT_SCALE)){
                // Equal to the override
                return null;
            }
        }

        // Check for equal to default value
        if(scale.equals(DEFAULT_SCALE)){
            // Only declare the component
            return NAME;
        }

        // Build full object
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("scale", String.format("%f, %f", scale.x(), scale.y()));
        result.put(NAME, data);
        return result;
    }

    /**
     * Get the scale factors
     *
     * @return Scale factors
     */
    public Vector2fc getScale(){
        return scale.toImmutable();
    }

    /**
     * Get the scale factors in 3D
     *
     * @return Scale factors
     */
    public Vector3fc getScale3D(){
        return (new Vector3f(scale, 1)).toImmutable();
    }

    /**
     * Set the scale
     *
     * @param newValue New scale
     */
    public void setScale(Vector2fc newValue){
        scale.set(newValue);
        owner.distributeEvent(new ScaleEvent(this));
    }

    /**
     * Add to the scale
     *
     * @param difference Value to add
     */
    public void addScale(Vector2fc difference){
        scale.add(difference);
        owner.distributeEvent(new ScaleEvent(this));
    }

    /**
     * Multiply the scale by a factor
     *
     * @param f Factor
     */
    public void mulScale(float f) {
        scale.mul(f);
        owner.distributeEvent(new ScaleEvent(this));
    }

    /**
     * Represents an event in the scale, such as a change
     */
    public static class ScaleEvent implements GameObjectEvent {
        /** Origin of the event */
        private ScaleComponent origin;

        /**
         * Construct the event from its origin
         *
         * @param origin Origin of the event
         */
        public ScaleEvent(ScaleComponent origin) {
            this.origin = origin;
        }

        @Override
        public Component getOrigin() {
            return origin;
        }
    }
}
