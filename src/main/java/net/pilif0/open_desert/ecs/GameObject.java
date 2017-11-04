package net.pilif0.open_desert.ecs;

import net.pilif0.open_desert.components.PositionComponent;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Represents a single game object.
 * This object is a container for components that provide the actual state and behaviour.
 * Each game object has at least the Position component.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class GameObject {
    /** Next handle to use. Starts at 0, last is -1. */
    private static int nextHandle = 0;

    /** List of all components (only one instance per component name) */
    private List<Component> components;
    /** Immutable unique (within each world) object handle */
    public final int handle;

    // Frequent components
    /** Position component */
    public final PositionComponent position;

    /**
     * Construct a new game object based on a template
     *
     * @param t Template to use
     */
    public GameObject(Template t){
        // Create the object based on the template generating a new handle for it
        //TODO
        position = new PositionComponent(); //TODO
        handle = newHandle();
    }

    /**
     * Construct a new game object from its handle and components
     *
     * @param handle Game object handle to use
     * @param components Components of the game object (reference is used, not copied)
     */
    protected GameObject(int handle, List<Component> components){
        this.handle = handle;
        this.components = components;
        position = (PositionComponent) getComponent("position");
    }

    /**
     * Add a component to this game object.
     * Returns {@code false} when a component with the same name is already present.
     *
     * @param c Component to add
     * @return Whether the list of components changed
     */
    public boolean addComponent(Component c){
        if(hasComponent(c.getName())){
            return false;
        }
        return components.add(c);
    }

    /**
     * Remove a component from this game object
     *
     * @param c Component to remove
     * @return Whether this game object had this component
     */
    public boolean removeComponent(Component c){
        return components.remove(c);
    }

    /**
     * Remove a component from this game object
     *
     * @param name Name of the component
     * @return Whether this game object had this component
     */
    public boolean removeComponent(String name){
        Iterator<Component> it = components.iterator();

        while(it.hasNext()){
            if(it.next().getName().equals(name)){
                it.remove();
            }
            return true;
        }

        return false;
    }

    /**
     * Retrieve a component from this game object based on a name
     *
     * @param name Name of the desired component
     * @return The desired component or {@code null} if this game object did not have that component
     */
    public Component getComponent(String name){
        return components.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Whether this game object has a component with the provided name
     *
     * @param name Name to look for
     * @return Whether this game object has a component with the name
     */
    public boolean hasComponent(String name){
        return components.stream()
                .anyMatch(c -> c.getName().equals(name));
    }

    /**
     * Check this game object against a condition
     *
     * @param c Condition to check against
     * @return Whether the game object passes the condition
     */
    public boolean check(Condition c){
        return c.check(this);
    }

    /**
     * Return a stream of components of this game object
     *
     * @return Stream of components
     */
    public Stream<Component> componentStream(){
        return components.stream();
    }

    /**
     * Return an array of components of this game object
     *
     * @return Array of components
     */
    public Component[] getComponents(){
        return components.toArray(new Component[components.size()]);
    }

    /**
     * Generate a new handle for a game object
     *
     * @return New handle
     */
    protected static int newHandle(){
        return nextHandle++;
    }

    /**
     * Distribute an event among all components of this game object
     *
     * @param e Event to distribute
     */
    public void distributeEvent(ComponentEvent e){
        components.stream().forEach(c -> c.handle(e));
    }
}
