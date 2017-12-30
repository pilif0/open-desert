package net.pilif0.open_desert.components;

import net.pilif0.open_desert.ecs.Component;
import net.pilif0.open_desert.ecs.ComponentFieldException;
import net.pilif0.open_desert.ecs.GameObject;
import net.pilif0.open_desert.ecs.GameObjectEvent;
import net.pilif0.open_desert.graphics.TextureAtlas;
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
    /** Default value for the texture atlas */
    public static final String DEFAULT_TEXTURE_ATLAS = "default.png";
    /** Default value for the texture index */
    public static final String DEFAULT_TEXTURE_INDEX = "0";
    /** Default value for the sprite dimensions */
    public static final String DEFAULT_DIMENSIONS = "1,1";

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
            throw new ComponentFieldException("Texture atlas could not be created.", e);
        }
        index = 0;
        dimensions = new Vector2f(1, 1);
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
    public Map<String, Object> getState() {
        Map<String, Object> result = new HashMap<>();
        result.put("atlas", atlas);
        result.put("index", index);
        result.put("dimensions", dimensions);
        return result;
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
        String valIndex = (String) overrides.getOrDefault("index", DEFAULT_TEXTURE_INDEX);
        index = Integer.parseInt(valIndex);

        // Dimensions are serialised as a String with two float values separated by ','
        String valDim = (String) overrides.getOrDefault("dimensions", DEFAULT_DIMENSIONS);
        String[] dims = valDim.split(",");
        dimensions = new Vector2f(Float.parseFloat(dims[0]), Float.parseFloat(dims[1]));
    }

    /**
     * Return the delta coordinates of the sprite in the texture atlas
     *
     * @return Delta coordinates of the sprite
     */
    public Vector2fc getTextureDelta(){ return atlas.getDeltaCoordinates(index); }

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