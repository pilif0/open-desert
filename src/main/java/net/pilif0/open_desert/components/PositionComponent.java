package net.pilif0.open_desert.components;

import net.pilif0.open_desert.ecs.Component;
import net.pilif0.open_desert.ecs.ComponentEvent;
import net.pilif0.open_desert.ecs.GameObject;
import org.joml.Vector2f;
import org.joml.Vector2fc;

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

    /** Position value */
    private Vector2f position;
    /** Map of the component state */
    private Map<String, Object> state;

    /**
     * Construct the component with position (0,0)
     */
    public PositionComponent(){
        this(null);
    }

    /**
     * Construct the component with the provided position
     *
     * @param position Position
     */
    public PositionComponent(Vector2fc position){
        if(position == null){
            this.position = new Vector2f();
        }else{
            this.position = new Vector2f(position);
        }
        state = new HashMap<>();
        state.put("position", position);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void handle(ComponentEvent e) {
        // So far there are no events for this component to handle
    }

    @Override
    public Map<String, Object> getState() {
        return state;
    }

    @Override
    public void onAttach(GameObject owner) {
        // Nothing to do
    }

    @Override
    public void onDetach(GameObject owner) {
        // Nothing to do
    }

    @Override
    public void overrideFields(Map<String, Object> overrides) {
        // Position is serialised as a String with two float values separated by ','
        String val = (String) overrides.get("position");
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
}
