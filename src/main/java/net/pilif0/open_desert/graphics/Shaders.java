package net.pilif0.open_desert.graphics;

import java.util.Map;
import java.util.TreeMap;

/**
 * Stores shaders for easy global access
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Shaders {
    /** The map that stores the shaders */
    public static final Map<Integer, ShaderProgram> map = new TreeMap<>();
    /** The last assigned identifier */
    private static int lastID = -1;

    /**
     * Add the shader
     *
     * @param program Shader to add
     * @return Identifier of the shader (for later access)
     */
    public static int add(ShaderProgram program){
        map.put(++lastID, program);
        return lastID;
    }

    /**
     * Remove the shader with the provided identifier
     *
     * @param id Identifier to remove
     * @return {@code true} when an entry was removed, {@code false} otherwise
     */
    public static boolean remove(int id){
        return map.remove(id) != null;
    }

    /**
     * Return the shader with the provided identifier
     *
     * @param id Identifier to get
     * @return The shader if present, {@code null} otherwise
     */
    public static ShaderProgram get(int id){
        return map.get(id);
    }

    /**
     * Clean up all the shaders
     */
    public static void cleanUp(){
        map.forEach((k, s) -> s.cleanUp());
    }
}
