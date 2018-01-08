package net.pilif0.open_desert.components.control;

import net.pilif0.open_desert.Game;
import net.pilif0.open_desert.components.MouseButtonSensitiveComponent;
import net.pilif0.open_desert.ecs.Component;
import net.pilif0.open_desert.ecs.GameObject;
import net.pilif0.open_desert.ecs.GameObjectEvent;
import net.pilif0.open_desert.ecs.Template;
import net.pilif0.open_desert.input.Action;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

/**
 * Makes the game object follow the mouse position when the middle mouse button is held
 *
 * @author Filip Smola
 * @version 1.0
 */
public class TestFollowControlComponent implements Component {
    /** Name of this component */
    public static final String NAME = "test_follow_control";
    /** Default move speed (in files) */
    public static final String DEFAULT_SPEED = "100";
    /** Default move speed */
    public static final float DEFAULT_SPEED_VALUE = 100f;
    /** Default precision (in files) */
    public static final String DEFAULT_PRECISION = "1";
    /** Default precision */
    public static final float DEFAULT_PRECISION_VALUE = 1f;

    /** Component's owner */
    private GameObject owner;
    /** Move speed */
    private float speed = DEFAULT_SPEED_VALUE;
    /** Distance at which no movement is made anymore */
    private float precision = DEFAULT_PRECISION_VALUE;

    // Input flag
    private boolean held = false;

    /**
     * Construct the component with default speed and precision
     */
    public TestFollowControlComponent(){}

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void handle(GameObjectEvent event) {
        if(event instanceof MouseButtonSensitiveComponent.MouseButtonEvent) {
            MouseButtonSensitiveComponent.MouseButtonEvent e = (MouseButtonSensitiveComponent.MouseButtonEvent) event;

            // Set the flag if appropriate
            if(e.button == GLFW_MOUSE_BUTTON_MIDDLE){
                if(e.action == Action.PRESS){
                    held = true;
                }else if(e.action == Action.RELEASE){
                    held = false;
                }
            }
        }else if(event instanceof GameObject.UpdateEvent){
            // Move if supposed to
            if(held) {
                GameObject.UpdateEvent e = (GameObject.UpdateEvent) event;
                Vector2fc origin = owner.position.getPosition();
                Vector2fc screenSp = Game.getInstance().getWindow().inputManager.getMousePosition();
                Vector2fc target = Game.getInstance().getCurrentState().getCamera().toWorldSpace(screenSp);

                // Get the direction
                Vector2f d = target.sub(origin, new Vector2f());

                // Skip if within precision
                if(d.length() <= precision) return;

                // Scale to speed and apply delta
                d.normalize().mul(speed * e.delta / (float) 1e9);

                // Apply
                owner.position.addPosition(d);
            }
        }
    }

    @Override
    public void onAttach(GameObject owner) {
        // Remember who the component is attached to and reset the flag
        held = false;
        this.owner = owner;
    }

    @Override
    public void onDetach(GameObject owner) {
        // Forget the owner and reset the flag
        held = false;
        this.owner = null;
    }

    @Override
    public void overrideFields(Map<String, Object> overrides) {
        // Speed is serialised as a single float
        Object speedVal = overrides.getOrDefault("speed", DEFAULT_SPEED);
        if(speedVal instanceof Number) {
            // When the value is a number
            speed = ((Number) speedVal).floatValue();
        }else if(speedVal instanceof String){
            // When the value is anything else or nothing
            speed = Float.parseFloat((String) speedVal);
        }

        // Precision is serialised as a single float
        Object precVal = overrides.getOrDefault("precision", DEFAULT_PRECISION);
        if(precVal instanceof Number) {
            // When the value is a number
            precision = ((Number) precVal).floatValue();
        }else if(precVal instanceof String){
            // When the value is anything else or nothing
            precision = Float.parseFloat((String) precVal);
        }
    }

    @Override
    public Object toYaml(Template t) {
        // Retrieve the template default
        Template.ComponentInfo info = t.getComponents().stream()
                .filter(i -> NAME.equals(i.name))
                .findFirst()
                .orElse(null);

        // Check template and default values
        Map<String, Object> data = new HashMap<>();

        // Check speed
        if(speed != DEFAULT_SPEED_VALUE){
            if(info != null){
                Object val = info.fieldOverrides.getOrDefault("speed", DEFAULT_SPEED);
                if(val instanceof Number) {
                    if(speed != ((Number) val).floatValue()){
                        // Not default and different from template --> must add to data
                        data.put("speed", speed);
                    }
                }else if(val instanceof String){
                    if(speed != Float.parseFloat((String) val)){
                        // Not default and different from template --> must add to data
                        data.put("speed", speed);
                    }
                }
            }else{
                // Not default and component not in template --> must add to data
                data.put("speed", speed);
            }
        }

        // Check precision
        if(precision != DEFAULT_PRECISION_VALUE){
            if(info != null){
                Object val = info.fieldOverrides.getOrDefault("precision", DEFAULT_SPEED);
                if(val instanceof Number) {
                    if(precision != ((Number) val).floatValue()){
                        // Not default and different from template --> must add to data
                        data.put("precision", precision);
                    }
                }else if(val instanceof String){
                    if(precision != Float.parseFloat((String) val)){
                        // Not default and different from template --> must add to data
                        data.put("precision", precision);
                    }
                }
            }else{
                // Not default and component not in template --> must add to data
                data.put("precision", precision);
            }
        }

        // Build and return the appropriate object
        if(data.isEmpty()){
            if(info != null){
                // No data overrides and component declared in template --> return null
                return null;
            }else{
                // No data overrides but component not declared in template --> declare the component
                return NAME;
            }
        }else{
            Map<String, Object> result = new HashMap<>();
            result.put(NAME, data);
            return result;
        }
    }
}
