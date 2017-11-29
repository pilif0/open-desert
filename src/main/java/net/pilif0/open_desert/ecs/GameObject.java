package net.pilif0.open_desert.ecs;

import net.pilif0.open_desert.Launcher;
import net.pilif0.open_desert.components.PositionComponent;
import net.pilif0.open_desert.components.RotationComponent;
import net.pilif0.open_desert.components.ScaleComponent;

import java.util.ArrayList;
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
    /** Reference to the template that this game object was created from ({@code null} when created manually) */
    public final Template template;

    // Frequent components
    /** Position component */
    public final PositionComponent position;
    /** Rotation component */
    public final RotationComponent rotation;
    /** Scale component */
    public final ScaleComponent scale;

    /**
     * Construct a new game object based on a template
     *
     * @param t Template to use
     */
    public GameObject(Template t){
        // Create the object based on the template generating a new handle for it
        template = t;
        components = new ArrayList<>();
        for(Template.ComponentInfo i : t.components){
            try {
                components.add(Components.instantiate(i));
            } catch (Exception e) {
                // Exception here means the game object cannot be properly created --> abort
                Launcher.getLog().log("GO:"+t.name, e);
                System.exit(1);
            }
        }
        position = (PositionComponent) getComponent("position");
        rotation = (RotationComponent) getComponent("rotation");
        scale = (ScaleComponent) getComponent("scale");
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
        components.forEach(c -> c.onAttach(this));
        position = (PositionComponent) getComponent("position");
        rotation = (RotationComponent) getComponent("rotation");
        scale = (ScaleComponent) getComponent("scale");
        template = null;
    }

    /**
     * Update this game object
     *
     * @param delta Delta time in ns
     */
    public void update(long delta){
        // Distribute as an event, because some components might not want to update (ex. just hold data)
        distributeEvent(new UpdateEvent(delta));
    }

    public void cleanUp(){
        // Distribute as an event, because some components might not have anything to clean up
        distributeEvent(new CleanUpEvent());
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
        boolean result = components.add(c);
        c.onAttach(this);
        return result;
    }

    /**
     * Remove a component from this game object
     *
     * @param c Component to remove
     * @return Whether this game object had this component
     */
    public boolean removeComponent(Component c){
        boolean result = components.remove(c);
        c.onDetach(this);
        return result;
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
            Component c = it.next();
            if(c.getName().equals(name)){
                c.onDetach(this);
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
    public void distributeEvent(GameObjectEvent e){
        components.stream().forEach(c -> c.handle(e));
    }

    /**
     * Event representing a game update
     */
    public static class UpdateEvent implements GameObjectEvent{
        /** Delta time in ns */
        public final long delta;

        /**
         * Construct the event from the delta time
         *
         * @param delta Delta time in ns
         */
        public UpdateEvent(long delta){
            this.delta = delta;
        }

        @Override
        public Component getOrigin() {
            return null;
        }
    }

    /**
     * Event representing game object cleaning up
     */
    public static class CleanUpEvent implements GameObjectEvent{
        @Override
        public Component getOrigin() {
            return null;
        }
    }
}
