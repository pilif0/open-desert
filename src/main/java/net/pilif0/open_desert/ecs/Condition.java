package net.pilif0.open_desert.ecs;

import java.util.ArrayList;
import java.util.Arrays;
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
     * Construct a condition from a number of component names
     *
     * @param names Names of desired components
     */
    public Condition(String... names){
        components = new ArrayList<String>(Arrays.asList(names));
    }

    /**
     * Add component to the desired list
     *
     * @param name Name of the component
     */
    public void addComponent(String name){
        components.add(name);
    }

    /**
     * Remove component from the desired list
     *
     * @param name Name of the component
     */
    public void removeComponent(String name){
        components.add(name);
    }

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
