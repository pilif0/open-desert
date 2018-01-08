package net.pilif0.open_desert.components.control;

import net.pilif0.open_desert.Game;
import net.pilif0.open_desert.components.KeyboardSensitiveComponent;
import net.pilif0.open_desert.components.SpriteComponent;
import net.pilif0.open_desert.ecs.Component;
import net.pilif0.open_desert.ecs.GameObject;
import net.pilif0.open_desert.ecs.GameObjectEvent;
import net.pilif0.open_desert.ecs.Template;
import net.pilif0.open_desert.input.Action;
import org.joml.Vector2f;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Control component that changes position based on WASD keys.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class WASDMovementControlComponent implements Component {
    /** Name of this component */
    public static final String NAME = "wasd_movement_control";
    /** Default speed value (in files) */
    public static final String DEFAULT_SPEED = "100";
    /** Default speed value */
    public static final float DEFAULT_SPEED_VALUE = 100f;

    /** Component's owner */
    private GameObject owner;
    /** Movement speed (omnidirectional) */
    private float speed = DEFAULT_SPEED_VALUE;
    
    // Key pressed flags
    private boolean w = false;
    private boolean a = false;
    private boolean s = false;
    private boolean d = false;

    /**
     * Construct the component with default speed
     */
    public WASDMovementControlComponent(){}

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void handle(GameObjectEvent event) {
        if(event instanceof KeyboardSensitiveComponent.KeyboardEvent) {
            // On keyboard update the flags
            KeyboardSensitiveComponent.KeyboardEvent e = (KeyboardSensitiveComponent.KeyboardEvent) event;
            
            // W
            if(e.key == GLFW_KEY_W){
                if(e.action == Action.PRESS){
                    w = true;
                }else if(e.action == Action.RELEASE){
                    w = false;
                }
            }
            // A
            if(e.key == GLFW_KEY_A){
                if(e.action == Action.PRESS){
                    a = true;
                }else if(e.action == Action.RELEASE){
                    a = false;
                }
            }
            // S
            if(e.key == GLFW_KEY_S){
                if(e.action == Action.PRESS){
                    s = true;
                }else if(e.action == Action.RELEASE){
                    s = false;
                }
            }
            // D
            if(e.key == GLFW_KEY_D){
                if(e.action == Action.PRESS){
                    d = true;
                }else if(e.action == Action.RELEASE){
                    d = false;
                }
            }
            
        }else if(event instanceof GameObject.UpdateEvent){
            // On update apply the movement
            Vector2f diff = new Vector2f();
            if(w){
                diff.add(0, -1);
            }
            if(s){
                diff.add(0, 1);
            }
            if(a){
                diff.add(-1, 0);
            }
            if(d){
                diff.add(1, 0);
            }

            //Normalize, scale to speed, and apply
            if(diff.x != 0 || diff.y != 0){
                diff.normalize();
                diff.mul(speed * (float) (((GameObject.UpdateEvent) event).delta / 1e9));
                owner.position.addPosition(diff);
            }
        }
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
        // Speed is serialised as a single float
        Object val = overrides.getOrDefault("speed", DEFAULT_SPEED);
        if(val instanceof Number) {
            // When the value is a number
            speed = ((Number) val).floatValue();
        }else if(val instanceof String){
            // When the value is anything else or nothing
            speed = Float.parseFloat((String) val);
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
            Object val = info.fieldOverrides.getOrDefault("speed", DEFAULT_SPEED);
            if( (val instanceof Number && speed == ((Number) val).floatValue()) ||
                    (val instanceof String && speed == Float.parseFloat((String) val)) ){
                // Equal to override
                return null;
            }
        }

        // Check for equal to default value
        if(speed == DEFAULT_SPEED_VALUE){
            // Only declare the component
            return NAME;
        }

        // Build full object
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("speed", speed);
        result.put(NAME, data);
        return result;
    }
}
