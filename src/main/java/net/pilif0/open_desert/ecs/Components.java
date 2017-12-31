package net.pilif0.open_desert.ecs;

import net.pilif0.open_desert.Launcher;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enables actions concerning all components.
 * Each component needs to have a nullary constructor in order to be instantiable.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Components {
    /** Map of all component declarations under the component names */
    public static final Map<String, ComponentDecl> declarations = new HashMap<>();

    /**
     * Instantiates a component by name (with default fields values)
     *
     * @param name Name of the component to instantiate
     * @return Component instance or {@code null} if a component with the provided name is not declared
     *
     * @throws IllegalAccessException If the attached class or its nullary constructor is not accessible.
     * @throws InstantiationException If the class cannot be instantiated (no nullary constructor or other error)
     * @throws IllegalStateException If not all required components are present
     */
    public static Component instantiate(String name) throws IllegalAccessException, InstantiationException {
        // Retrieve the declaration
        ComponentDecl declaration = declarations.get(name);
        if(declaration == null){
            return null;
        }

        // Check presence of required components
        for(String r : declaration.required){
            if(!declarations.containsKey(r)){
                throw new IllegalStateException(String.format("The component '%s' is not present and is required to instantiate '%s'", r, name));
            }
        }

        // Instantiate and return the component class
        Component c = declaration.attachedClass.newInstance();
        return c;
    }

    /**
     * Instantiates a component from template's component information
     *
     * @param info Component information
     * @return Component instance of {@code null} if a component with the provided name is not declared
     *
     * @throws IllegalAccessException If the attached class or its nullary constructor is not accessible.
     * @throws InstantiationException If the class cannot be instantiated (no nullary constructor or other error)
     * @throws IllegalStateException If not all required components are present
     */
    public static Component instantiate(Template.ComponentInfo info) throws InstantiationException, IllegalAccessException {
        // Instantiate the component
        Component component = instantiate(info.name);

        // Provide override information to the component
        if(info.fieldOverrides != null){
            component.overrideFields(info.fieldOverrides);
        }

        // Return it
        return component;
    }

    /**
     * Reads all component declarations from a YAML file
     *
     * @param p Path to the file to read
     * @throws IOException If an I/O error occurs
     */
    public static void from(Path p) throws IOException{
        // Prepare the YAML parser
        Yaml yaml = new Yaml();

        // Read the list of component declarations
        List<Object> decls = yaml.load(Files.newInputStream(p, StandardOpenOption.READ));
        for(Object o : decls){
            try {
                ComponentDecl d = ComponentDecl.fromYAML(o);
                declarations.put(d.name, d);
            } catch (Exception e) {
                // Skip the declaration and log the exception
                Launcher.getLog().log("ComponentDecl.fromYAML" , e);
            }
        }
    }

    /**
     * Declares a component and all the information about how to build it
     */
    public static class ComponentDecl{
        /** Name of the component */
        public final String name;
        /** Class containing the component behaviour */
        public final Class<Component> attachedClass;
        /** Names of the required components (checked when instantiating) */
        public final String[] required;

        /**
         * Construct a component declaration from all its data
         *
         * @param name Name of the component
         * @param attachedClass Class containing the component behaviour
         * @param required Names of the required components
         */
        private ComponentDecl(String name, Class<Component> attachedClass, String[] required) {
            this.name = name;
            this.attachedClass = attachedClass;
            this.required = required;
        }

        /**
         * Build a component declaration from an object parsed from a component declaration YAML file
         *
         * @param src Object to read
         * @return Component declaration object
         * @throws ClassNotFoundException When the attached class cannot be found
         * @throws IllegalArgumentException When the attached class does not implement the Component interface
         */
        private static ComponentDecl fromYAML(Object src) throws ClassNotFoundException{
            // Component declaration is a map of strings
            Map<String, String> data = (Map<String, String>) src;
            String name = data.get("name");
            Class<?> attachedClass = Class.forName(data.get("class"));
            if(!Component.class.isAssignableFrom(attachedClass)){
                throw new IllegalArgumentException("The declared attached class does not implement the Component interface.");
            }
            String reqs = data.get("required");
            String[] required = ("none".equals(reqs)) ? new String[0] : reqs.split(",");
            for(int i = 0; i < required.length; i++) required[i] = required[i].trim();
            return new ComponentDecl(name, (Class<Component>) attachedClass, required);
        }
    }
}
