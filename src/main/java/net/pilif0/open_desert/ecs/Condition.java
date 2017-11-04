package net.pilif0.open_desert.ecs;

import java.util.ArrayList;
import java.util.List;

/**
 * Compiles together a set of conditions for game objects for quicker filtering
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Condition {
    /** Components the game object needs to have */
    private List<String> components;

    /**
     * Whether the game object satisfies this condition
     *
     * @param go Game object to check
     * @return Whether the game object satisfies this condition
     */
    public boolean check(GameObject go){
        // Check components
        if(components.size() != 0){
            List<String> copy = new ArrayList<>(components);
            for(Component c : go.getComponents()){
                copy.remove(c.getName());
                if(copy.isEmpty()){
                    break;
                }
            }

            // Copy not emptied --> not all components present
            if(!copy.isEmpty()){
                return false;
            }
        }

        //Everything passed
        return true;
    }
}
