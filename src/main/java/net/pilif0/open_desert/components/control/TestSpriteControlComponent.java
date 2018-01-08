package net.pilif0.open_desert.components.control;

import net.pilif0.open_desert.Launcher;
import net.pilif0.open_desert.components.KeyboardSensitiveComponent;
import net.pilif0.open_desert.components.SpriteComponent;
import net.pilif0.open_desert.ecs.*;
import net.pilif0.open_desert.input.Action;
import net.pilif0.open_desert.util.Severity;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

/**
 * Control component for the test sprite.
 * Increments and decrements sprite index.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class TestSpriteControlComponent implements Component {
    /** Name of this component */
    public static final String NAME = "test_sprite_control";
    /** Name of the increment key (in GLFW fields) */
    public static final String DEFAULT_INCREMENT_KEY = "RIGHT";
    /** Default increment key code */
    public static final int DEFAULT_INCREMENT_KEY_VALUE = GLFW_KEY_RIGHT;
    /** Name of the decrement key (in GLFW fields) */
    public static final String DEFAULT_DECREMENT_KEY = "LEFT";
    /** Default decrement key code */
    public static final int DEFAULT_DECREMENT_KEY_VALUE = GLFW_KEY_LEFT;

    /** Component's owner */
    private GameObject owner;
    /** Increment key code */
    private int increment = DEFAULT_INCREMENT_KEY_VALUE;
    /** Decrement key code */
    private int decrement = DEFAULT_DECREMENT_KEY_VALUE;

    /**
     * Construct the component with default keys (left for decrement, right for increment)
     */
    public TestSpriteControlComponent(){}

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void handle(GameObjectEvent event) {
        if(event instanceof KeyboardSensitiveComponent.KeyboardEvent) {
            SpriteComponent spriteComponent = (SpriteComponent) owner.getComponent("sprite");
            KeyboardSensitiveComponent.KeyboardEvent e = (KeyboardSensitiveComponent.KeyboardEvent) event;

            // Increment segment on right arrow
            if (e.key == increment && e.action == Action.RELEASE) {
                int after = (spriteComponent.getIndex() + 1) % spriteComponent.getAtlas().segments;
                spriteComponent.setIndex(after);
            }

            // Decrement segment on left arrow
            if (e.key == decrement && e.action == Action.RELEASE) {
                int after = (spriteComponent.getIndex() - 1) % spriteComponent.getAtlas().segments;
                if (after < 0) after += spriteComponent.getAtlas().segments;
                spriteComponent.setIndex(after);
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
        // Keys are serialised as names of the GLFW fields without the "GLFW_KEY_" prefix or direct integer keycodes
        Object valInc = overrides.getOrDefault("increment", DEFAULT_INCREMENT_KEY);
        if(valInc instanceof Number){
            // Keycode
            increment = ((Number) valInc).intValue();
        }else if(valInc instanceof String){
            // Field name
            try {
                increment = GLFW.class.getDeclaredField("GLFW_KEY_"+valInc.toString().toUpperCase()).getInt(null);
            } catch (IllegalAccessException e) {
                // The fields are all public so this should never happen
                Launcher.getLog().log(Severity.SEVERE, "Reflection", "GLFW key fields are not accessible.");
                System.exit(1);
            } catch (NoSuchFieldException e) {
                throw new ComponentFieldException("Increment field contains an invalid key");
            }
        }else{
            // Invalid
            throw new ComponentFieldException("Increment field is not a keycode nor a field name");
        }

        Object valDec = overrides.getOrDefault("decrement", DEFAULT_DECREMENT_KEY);
        if(valDec instanceof Number){
            // Keycode
            decrement = ((Number) valDec).intValue();
        }else if(valDec instanceof String){
            // Field name
            try {
                decrement = GLFW.class.getDeclaredField("GLFW_KEY_"+valDec.toString().toUpperCase()).getInt(null);
            } catch (IllegalAccessException e) {
                // The fields are all public so this should never happen
                Launcher.getLog().log(Severity.SEVERE, "Reflection", "GLFW key fields are not accessible.");
                System.exit(1);
            } catch (NoSuchFieldException e) {
                throw new ComponentFieldException("Decrement field contains an invalid key");
            }
        }else{
            // Invalid
            throw new ComponentFieldException("Decrement field is not a keycode nor a field name");
        }
    }

    @Override
    // Stores the keys as keycodes
    public Object toYaml(Template t) {
        // Retrieve the template default
        Template.ComponentInfo info = t.getComponents().stream()
                .filter(i -> NAME.equals(i.name))
                .findFirst()
                .orElse(null);

        // Check template and default values
        Map<String, Object> data = new HashMap<>();

        // Check increment
        if(increment != DEFAULT_INCREMENT_KEY_VALUE){
            if(info != null){
                Object val = info.fieldOverrides.getOrDefault("increment", DEFAULT_INCREMENT_KEY);
                if(val instanceof Number){
                    if(increment != ((Number) val).intValue()){
                        // Not default and different from template --> must add to data
                        data.put("increment", increment);
                    }
                }else if(val instanceof String){
                    // Field name
                    try {
                        int valResolved = GLFW.class.getDeclaredField("GLFW_KEY_"+val.toString().toUpperCase()).getInt(null);
                        if(increment != valResolved){
                            // Not default and different from template --> must add to data
                            data.put("increment", increment);
                        }
                    } catch (IllegalAccessException e) {
                        // The fields are all public so this should never happen
                        Launcher.getLog().log(Severity.SEVERE, "Reflection", "GLFW key fields are not accessible.");
                        System.exit(1);
                    } catch (NoSuchFieldException e) {
                        throw new ComponentFieldException("Increment field contains an invalid key");
                    }
                }
            }else{
                // Not default and component not in template --> must add to data
                data.put("increment", increment);
            }
        }

        // Check decrement
        if(decrement != DEFAULT_DECREMENT_KEY_VALUE){
            if(info != null){
                Object val = info.fieldOverrides.getOrDefault("decrement", DEFAULT_DECREMENT_KEY);
                if(val instanceof Number){
                    if(decrement != ((Number) val).intValue()){
                        // Not default and different from template --> must add to data
                        data.put("decrement", decrement);
                    }
                }else if(val instanceof String){
                    // Field name
                    try {
                        int valResolved = GLFW.class.getDeclaredField("GLFW_KEY_"+val.toString().toUpperCase()).getInt(null);
                        if(decrement != valResolved){
                            // Not default and different from template --> must add to data
                            data.put("decrement", decrement);
                        }
                    } catch (IllegalAccessException e) {
                        // The fields are all public so this should never happen
                        Launcher.getLog().log(Severity.SEVERE, "Reflection", "GLFW key fields are not accessible.");
                        System.exit(1);
                    } catch (NoSuchFieldException e) {
                        throw new ComponentFieldException("Decrement field contains an invalid key");
                    }
                }
            }else{
                // Not default and component not in template --> must add to data
                data.put("decrement", decrement);
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
