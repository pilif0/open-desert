package net.pilif0.open_desert.components;

import net.pilif0.open_desert.Launcher;
import net.pilif0.open_desert.ecs.*;
import net.pilif0.open_desert.graphics.TextureAtlas;
import net.pilif0.open_desert.util.Severity;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains information needed to render the game object as a sprite
 *
 * @author Filip Smola
 * @version 1.0
 */
public class SpriteComponent implements Component {
    /** Name of this component */
    public static final String NAME = "sprite";
    /** Default value for the texture atlas (in files) */
    public static final String DEFAULT_TEXTURE_ATLAS = "textures/default.png";
    /** Default value for the texture index (in files) */
    public static final String DEFAULT_TEXTURE_INDEX = "0";
    /** Default value for the texture index */
    public static final int DEFAULT_TEXTURE_INDEX_VALUE = 0;
    /** Default value for the sprite dimensions (in files) */
    public static final String DEFAULT_DIMENSIONS = "1,1";
    /** Default value for the sprite dimensions */
    public static final Vector2fc DEFAULT_DIMENSIONS_VALUE = new Vector2f(1,1);

    /** Texture atlas the sprite is from */
    private TextureAtlas atlas;
    /** Index of the sprite in the texture atlas */
    private int index;
    /** Dimensions of the sprite */
    private Vector2f dimensions;

    /**
     * Construct the component with the default atlas, index and dimensions
     */
    public SpriteComponent(){
        try {
            atlas = TextureAtlas.from(Paths.get(DEFAULT_TEXTURE_ATLAS));
        } catch (IOException e) {
            // Pass the exception up (causes instantiation to fail and the system to abort)
            e.printStackTrace();
            throw new ComponentFieldException("Texture atlas could not be created.", e);
        }
        index = DEFAULT_TEXTURE_INDEX_VALUE;
        dimensions = new Vector2f(DEFAULT_DIMENSIONS_VALUE);
    }

    /**
     * Construct the component with the provided atlas, index and dimensions
     *
     * @param atlas Texture atlas to use
     * @param index Index of the sprite in the texture atlas
     * @param dimensions Dimensions of the sprite
     */
    public SpriteComponent(TextureAtlas atlas, int index, Vector2fc dimensions){
        this.atlas = atlas;
        this.index = index;
        this.dimensions = new Vector2f(dimensions);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void handle(GameObjectEvent e) {
        // So far there are no events for this component to handle
    }

    @Override
    public void onAttach(GameObject owner) {}

    @Override
    public void onDetach(GameObject owner) {}

    @Override
    public void overrideFields(Map<String, Object> overrides) {
        // Atlas is serialised as the path to the atlas file
        String valAtlas = (String) overrides.getOrDefault("atlas", DEFAULT_TEXTURE_ATLAS);
        try {
            atlas = TextureAtlas.from(Paths.get(valAtlas));
        } catch (IOException e) {
            // Pass the exception up (causes instantiation to fail and the system to abort)
            throw new ComponentFieldException("Texture atlas could not be created.", e);
        }

        // Index is serialised as a String with one integer value
        Object valIndex = overrides.getOrDefault("index", DEFAULT_TEXTURE_INDEX);
        if(valIndex instanceof Number) {
            // When the value is a number
            index = ((Number) valIndex).intValue();
        }else if(valIndex instanceof String){
            // When the value is anything else or nothing
            index = Integer.parseInt((String) valIndex);
        }

        // Dimensions are serialised as a String with two float values separated by ','
        String valDim = (String) overrides.getOrDefault("dimensions", DEFAULT_DIMENSIONS);
        String[] dims = valDim.split(",");
        dimensions = new Vector2f(Float.parseFloat(dims[0]), Float.parseFloat(dims[1]));
    }

    @Override
    public Object toYaml(Template t) {
        // Retrieve the template default
        Template.ComponentInfo info = t.getComponents().stream()
                .filter(i -> NAME.equals(i.name))
                .findFirst()
                .orElse(null);

        // Check template and default values
        Map<String, Object> data = new HashMap<>();

        // Check atlas
        if(!Paths.get(DEFAULT_TEXTURE_ATLAS).equals(atlas.path)){
            if(info != null) {
                String valAtlas = (String) info.fieldOverrides.getOrDefault("atlas", DEFAULT_TEXTURE_ATLAS);
                if (!Paths.get(valAtlas).equals(atlas.path)) {
                    // Not default and different from template --> must add to data
                    data.put("atlas", atlas.path.toString());   //TODO: validate the path written is sensible
                }
            }else{
                // Not default and different from template --> must add to data
                data.put("atlas", atlas.path.toString());   //TODO: validate the path written is sensible
            }
        }

        // Check index
        if(index != DEFAULT_TEXTURE_INDEX_VALUE){
            if(info != null){
                Object valIndex = info.fieldOverrides.getOrDefault("index", DEFAULT_TEXTURE_INDEX);
                if(valIndex instanceof Number) {
                    if(index != ((Number) valIndex).intValue()){
                        // Not default and different from template --> must add to data
                        data.put("index", index);
                    }
                }else if(valIndex instanceof String){
                    if(index != Integer.parseInt((String) valIndex)){
                        // Not default and different from template --> must add to data
                        data.put("index", index);
                    }
                }
            }else{
                // Not default and component not in template --> must add to data
                data.put("index", index);
            }
        }

        // Check dimensions
        if(!dimensions.equals(DEFAULT_DIMENSIONS_VALUE)){
            if(info != null){
                String valDim = (String) info.fieldOverrides.getOrDefault("dimensions", DEFAULT_DIMENSIONS);
                String[] dims = valDim.split(",");
                dimensions = new Vector2f(Float.parseFloat(dims[0]), Float.parseFloat(dims[1]));
                if(dimensions.x() != Float.parseFloat(dims[0]) && dimensions.y() != Float.parseFloat(dims[1])){
                    // Not default and different from template --> must add to data
                    data.put("dimensions", String.format("%f, %f", dimensions.x(), dimensions.y()));
                }
            }else{
                // Not default and component not in template --> must add to data
                data.put("dimensions", String.format("%f, %f", dimensions.x(), dimensions.y()));
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

    /**
     * Return the delta coordinates of the sprite in the texture atlas
     *
     * @return Delta coordinates of the sprite
     */
    public Vector2fc getTextureDelta(){ return atlas.getDeltaCoordinates(index); }

    /**
     * Return the sprite index
     *
     * @return Sprite index
     */
    public int getIndex(){ return index; }

    /**
     * Return the texture atlas
     *
     * @return Texture atlas
     */
    public TextureAtlas getAtlas() {
        return atlas;
    }

    /**
     * Return the ID of the texture atlas
     *
     * @return ID of the texture atlas
     */
    public int getTextureID(){ return atlas.getID(); }

    /**
     * Return original dimensions of the sprite
     *
     * @return Original dimensions of the sprite
     */
    public Vector2fc getDimensions() {
        return dimensions;
    }

    /**
     * Set the texture atlas
     *
     * @param atlas New value
     */
    public void setAtlas(TextureAtlas atlas) {
        this.atlas = atlas;
    }

    /**
     * Set sprite index
     *
     * @param index New value
     */
    public void setIndex(int index) {
        this.index = index;
    }
}
