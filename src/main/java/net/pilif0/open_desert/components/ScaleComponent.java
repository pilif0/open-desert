package net.pilif0.open_desert.components;

import net.pilif0.open_desert.ecs.Component;
import net.pilif0.open_desert.ecs.GameObject;
import net.pilif0.open_desert.ecs.GameObjectEvent;
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
    public static final String DEFAULT_SCALE = "1,1";

    /** Scale factors */
    private Vector2f scale;
    /** Component's owner */
    private GameObject owner;

    /**
     * Construct the component with scale (1,1)
     */
    public ScaleComponent(){
        scale = new Vector2f(1, 1);
    }

    /**
     * Construct the component with the provided scale
     *
     * @param scale Scale factors
     */
    public ScaleComponent(Vector2fc scale){
        this.scale = new Vector2f(this.scale);
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
    public Map<String, Object> getState(){
        Map<String, Object> result = new HashMap<>();
        result.put("scale", scale.toImmutable());
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
        // Scale is serialised as a String with two float values separated by ','
        String val = (String) overrides.getOrDefault("scale", DEFAULT_SCALE);
        String[] p = val.split(",");
        scale.set(Float.parseFloat(p[0]), Float.parseFloat(p[1]));
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
