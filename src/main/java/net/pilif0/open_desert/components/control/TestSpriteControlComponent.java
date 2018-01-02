package net.pilif0.open_desert.components.control;

import net.pilif0.open_desert.Launcher;
import net.pilif0.open_desert.components.KeyboardSensitiveComponent;
import net.pilif0.open_desert.components.SpriteComponent;
import net.pilif0.open_desert.ecs.Component;
import net.pilif0.open_desert.ecs.ComponentFieldException;
import net.pilif0.open_desert.ecs.GameObject;
import net.pilif0.open_desert.ecs.GameObjectEvent;
import net.pilif0.open_desert.input.Action;
import net.pilif0.open_desert.util.Severity;
import org.lwjgl.glfw.GLFW;

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
    /** Name of the decrement key (in GLFW fields) */
    public static final String DEFAULT_DECREMENT_KEY = "LEFT";

    /** Component's owner */
    private GameObject owner;
    /** Increment key code */
    private int increment = GLFW_KEY_RIGHT;
    /** Decrement key code */
    private int decrement = GLFW_KEY_LEFT;

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
        // Keys are serialised as names of the GLFW fields without the "GLFW_KEY_" prefix
        String valInc = (String) overrides.getOrDefault("increment", DEFAULT_INCREMENT_KEY);
        String valDec = (String) overrides.getOrDefault("decrement", DEFAULT_DECREMENT_KEY);
        try {
            increment = GLFW.class.getDeclaredField("GLFW_KEY_"+valInc.toUpperCase()).getInt(null);
        } catch (IllegalAccessException e) {
            // The fields are all public so this should never happen
            Launcher.getLog().log(Severity.SEVERE, "Reflection", "GLFW key fields are not accessible.");
            System.exit(1);
        } catch (NoSuchFieldException e) {
            throw new ComponentFieldException("Increment field contains an invalid key");
        }
        try {
            decrement = GLFW.class.getDeclaredField("GLFW_KEY_"+valDec.toUpperCase()).getInt(null);
        } catch (IllegalAccessException e) {
            // The fields are all public so this should never happen
            Launcher.getLog().log(Severity.SEVERE, "Reflection", "GLFW key fields are not accessible.");
            System.exit(1);
        } catch (NoSuchFieldException e) {
            throw new ComponentFieldException("Decrement field contains an invalid key");
        }
    }
}
