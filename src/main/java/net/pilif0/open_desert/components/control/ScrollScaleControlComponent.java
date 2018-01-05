package net.pilif0.open_desert.components.control;

import net.pilif0.open_desert.components.ScrollSensitiveComponent;
import net.pilif0.open_desert.ecs.Component;
import net.pilif0.open_desert.ecs.GameObject;
import net.pilif0.open_desert.ecs.GameObjectEvent;
import net.pilif0.open_desert.input.Action;

import java.util.Map;

/**
 * Control component that changes scale based on scrolling
 *
 * @author Filip Smola
 * @version 1.0
 */
public class ScrollScaleControlComponent implements Component {
    /** Name of this component */
    public static final String NAME = "scroll_scale_control";
    /** Default speed value */
    public static final String DEFAULT_FACTOR = "1.25";

    /** Component's owner */
    private GameObject owner;
    /** Scaling factor per scroll step */
    private float factor = 1.25f;

    /**
     * Construct the component with default factor
     */
    public ScrollScaleControlComponent(){}

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void handle(GameObjectEvent event) {
        if(event instanceof ScrollSensitiveComponent.ScrollEvent) {
            ScrollSensitiveComponent.ScrollEvent e = (ScrollSensitiveComponent.ScrollEvent) event;
            float f = (float) Math.abs(e.y) * factor;
            if(e.y > 0) f = 1/f;    // Up is scale down
            owner.scale.mulScale(f);
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
        Object val = overrides.getOrDefault("factor", DEFAULT_FACTOR);
        if(val instanceof Number) {
            // When the value is a number
            factor = ((Number) val).floatValue();
        }else if(val instanceof String){
            // When the value is anything else or nothing
            factor = Float.parseFloat((String) val);
        }
    }
}
