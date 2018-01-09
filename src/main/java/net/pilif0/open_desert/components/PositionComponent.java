package net.pilif0.open_desert.components;

import net.pilif0.open_desert.ecs.Component;
import net.pilif0.open_desert.ecs.GameObjectEvent;
import net.pilif0.open_desert.ecs.GameObject;
import net.pilif0.open_desert.ecs.Template;
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
    public static final Vector2fc DEFAULT_POSITION = new Vector2f();

    /** Position value */
    private Vector2f position = new Vector2f(DEFAULT_POSITION);
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
        // Position is serialised as a String with two float values separated by ','
        Object val = overrides.getOrDefault("position", null);
        if(val instanceof String){
            String[] p = ((String) val).split(",");
            position.set(Float.parseFloat(p[0]), Float.parseFloat(p[1]));
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
            Object val = info.fieldOverrides.getOrDefault("position", null);
            if(val != null && val instanceof String){
                String[] p = ((String) val).split(",");
                if(position.x() == Float.parseFloat(p[0]) && position.y() == Float.parseFloat(p[1])){
                    // Equal to the override
                    return null;
                }
            }else if(val == null && position.equals(DEFAULT_POSITION)){
                // Equal to the override
                return null;
            }
        }

        // Check for equal to default value
        if(position.equals(DEFAULT_POSITION)){
            // Only declare the component
            return NAME;
        }

        // Build full object
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("position", String.format("%f, %f", position.x(), position.y()));
        result.put(NAME, data);
        return result;
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
        owner.distributeEvent(new PositionEvent(this));
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
