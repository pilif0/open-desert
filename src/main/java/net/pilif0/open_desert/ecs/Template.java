package net.pilif0.open_desert.ecs;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Template representation for putting together components into game objects.
 * Components of the parent template get merged into the templates components on first component information request
 * (cascades up the inheritance tree).
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Template {
    /** Map of all templates under their names */
    private static final Map<String, Template> templates = new HashMap<>();

    /** Name of the template */
    public final String name;
    /** Parent of the template */
    public final Template parent;
    /** List of information objects to construct the appropriate components with the right state */
    private final List<ComponentInfo> components;
    /** Whether the parent's components have been merged into this template's components */
    private boolean componentsMerged = false;

    /**
     * Construct a template directly from its data
     *
     * @param name Name of the template
     * @param parent Parent of the template
     * @param components Component information objects
     */
    private Template(String name, Template parent, List<ComponentInfo> components){
        this.name = name;
        this.parent = parent;
        this.components = components;
        templates.put(name, this);
    }

    /**
     * Construct a template directly from a YAML file
     *
     * @param p Path to the file
     * @throws IOException If an I/O error occurs
     */
    public Template(Path p) throws IOException{
        // Preare the YAML parser
        Yaml yaml = new Yaml();

        // Read the template root (template -> it is a map)
        Map<String, Object> root = yaml.load(Files.newInputStream(p, StandardOpenOption.READ));

        // Process the read map
        name = root.get("name").toString();
        parent = templates.get(root.get("parent").toString());

        List<Object> comps = (List<Object>) root.get("components");
        components = new ArrayList<>();
        comps.forEach(o -> components.add(ComponentInfo.fromYAML(o)));

        // Add the template to the list
        templates.put(name, this);
    }

    /**
     * Merge parent's components into this template's components
     *
     * @return Whether merge was successful
     */
    // Can only fail with an exception
    private boolean mergeComponents() {
        // Termination - no parent --> no merging needed
        if(parent == null){
            return true;
        }

        //Retrieve the parent's component information
        List<ComponentInfo> parental = parent.getComponents();

        // For all of this template's components, either add it or merge it
        List<ComponentInfo> result = new ArrayList<>(parental);
        for(ComponentInfo child : components){
            // Compare with each already present component
            ListIterator<ComponentInfo> it = result.listIterator();
            boolean wasPresent = false;

            while(it.hasNext()){
                ComponentInfo present = it.next();

                // Compare based on names
                if(present.name.equals(child.name)){
                    it.remove();
                    it.add(ComponentInfo.merge(present, child));
                    wasPresent = true;
                    break;
                }
            }

            // Add if it wasn't in parental
            if(!wasPresent){
                result.add(child);
            }
        }

        return true;
    }

    /**
     * Return the full component information of this template (including parental)
     *
     * @return Full component information
     */
    public List<ComponentInfo> getComponents() {
        if(!componentsMerged){
            componentsMerged = mergeComponents();
        }
        return components;
    }

    /**
     * Return a template by name
     *
     * @param name Name to seek
     * @return Template or {@code null} if there isn't one with the name
     */
    public static Template get(String name){ return templates.get(name); }

    /**
     * Information about a component in a template - its name and overrides for fields
     */
    public static class ComponentInfo{
        /** Name of the component */
        public final String name;
        /** Map of new values under the field names */
        public final Map<String, Object> fieldOverrides;

        /**
         * Construct an information object from the component name and the field overrides
         *
         * @param name Component name
         * @param fieldOverrides Field overrides
         */
        public ComponentInfo(String name, Map<String, Object> fieldOverrides){
            this.name = name;
            this.fieldOverrides = fieldOverrides;
        }

        /**
         * Build an information object from an object parsed from a template YAML file
         *
         * @param src Object to read
         * @return Component information object
         */
        public static ComponentInfo fromYAML(Object src){
            // Source is String when there are no overrides
            if(src instanceof String){
                return new ComponentInfo((String) src, new HashMap<>());
            }

            // Otherwise handle the overrides
            Map<String, Object> root = (Map<String, Object>) src;
            String name = root.keySet().stream().findFirst().get(); // Name is the first (and only) key of the root map
            Map<String, Object> fieldOverrides = (Map<String, Object>) root.get(name);
            return new ComponentInfo(name, fieldOverrides);
        }

        /**
         * Merges two component infos into one, overriding values in parent with values in child
         *
         * @param parent Parent component info
         * @param child Child component info
         * @return Merged component info
         */
        public static ComponentInfo merge(ComponentInfo parent, ComponentInfo child){
            // Check names are equal
            if(!parent.name.equals(child.name)){
                throw new IllegalStateException(String.format("Cannot merge component information for '%s' and '%s'", parent.name, child.name));
            }

            // Assume parent overrides
            Map<String, Object> overrides = new HashMap<>(parent.fieldOverrides);

            // Overwrite with child overrides when necessary
            overrides.putAll(child.fieldOverrides);

            // Create and return the result
            return new ComponentInfo(parent.name, overrides);
        }
    }
}
