package net.pilif0.open_desert;

import net.pilif0.open_desert.entities.ColorEntity;
import net.pilif0.open_desert.entities.DynamicColorEntity;
import net.pilif0.open_desert.entities.SpriteEntity;
import net.pilif0.open_desert.entities.TextureEntity;
import net.pilif0.open_desert.geometry.Transformation;
import net.pilif0.open_desert.graphics.*;
import net.pilif0.open_desert.graphics.shapes.ColorShape;
import net.pilif0.open_desert.graphics.shapes.Shape;
import net.pilif0.open_desert.graphics.shapes.SpriteShape;
import net.pilif0.open_desert.graphics.shapes.TextureShape;
import net.pilif0.open_desert.input.Action;
import net.pilif0.open_desert.input.InputManager;
import net.pilif0.open_desert.state.GameState;
import net.pilif0.open_desert.util.Color;
import org.joml.*;
import org.joml.Math;

import java.io.IOException;
import java.nio.file.Paths;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Represents the intro game-state
 *
 * @author Filip Smola
 * @version 1.0
 */
public class IntroState extends GameState{
    /** The background colour */
    public static final Color CLEAR_COLOR = new Color(0x00_00_00_ff);
    /** The camera movement speed */
    public static final float CAMERA_SPEED = 750f;
    /** The entity movement speed */
    public static final float ENTITY_SPEED = 250f;
    /** The textured square shape */
    public static final TextureShape SQUARE_SHAPE;
    /** The rainbow square shape */
    public static final ColorShape RAINBOW_SQUARE;
    /** The basic square shape */
    public static final Shape BASIC_SQUARE;
    /** The sprite shape */
    public static final SpriteShape SPRITE_SHAPE;
    /** The sprite texture atlas */
    public static final TextureAtlas SPRITE_TEXTURE_ATLAS;
    /** The test texture */
    public static final Texture TEST_TEXTURE;

    static{
        //Read the texture atlas
        try{
            SPRITE_TEXTURE_ATLAS = new TextureAtlas(Paths.get("textures/atlas.png"), 64, 64);
        }catch(IOException e){
            Launcher.getLog().log("Texture", e);
            throw new RuntimeException("Crash because of texture atlas");
        }

        //Parse the shapes
        SQUARE_SHAPE = TextureShape.parse(Paths.get("shapes/TexturedSquare.shape"));
        RAINBOW_SQUARE = ColorShape.parse(Paths.get("shapes/RainbowSquare.shape"));
        BASIC_SQUARE = Shape.parse(Paths.get("shapes/Square.shape"));
        SPRITE_SHAPE = new SpriteShape(64, 64, SPRITE_TEXTURE_ATLAS.width, SPRITE_TEXTURE_ATLAS.height);

        //Read the texture
        try {
            TEST_TEXTURE = new Texture(Paths.get("textures/test.png"));
        } catch (IOException e) {
            Launcher.getLog().log("Texture", e);
            throw new RuntimeException("Crash because of texture");
        }
    }

    /** The entity to draw */
    private TextureEntity entity;
    /** The static entity */
    private ColorEntity staticEntity;
    /** The pulsating entity */
    private DynamicColorEntity pulsatingEntity;
    /** The sprite entity */
    private SpriteEntity sprite;
    /** The phase of the pulsating entity */
    private double pulsePhase;
    /** The camera */
    private PerpendicularCamera camera;

    /**
     * Constructs the state
     */
    public IntroState(){
        super();

        //Create the camera
        camera = new PerpendicularCamera(new Vector2f(0, 0), Game.getInstance().getWindow().getResolution());

        //Have the camera listen to window resolution change
        Game.getInstance().getWindow().resolutionMultiplexer.register(e -> {
            camera.setDimensions(new Vector2i(e.newX, e.newY));
        });

        //Create the entity and scale it by factor of 100
        entity = new TextureEntity(SQUARE_SHAPE, TEST_TEXTURE);
        entity.getTransformation()
                .setScale(new Vector2f(100, 100));

        //Create the static entity
        staticEntity = new ColorEntity(RAINBOW_SQUARE);
        staticEntity.getTransformation()
                .setScale(new Vector2f(50, 50))
                .translate(new Vector2f(450, 250));

        //Create the pulsating entity
        pulsatingEntity = new DynamicColorEntity(BASIC_SQUARE, new Transformation(), new Color(0x00_00_00_ff));
        pulsatingEntity.getTransformation()
                .setScale(new Vector2f(150, 50))
                .translate(new Vector2f(200, 600));

        //Create the sprite entity
        sprite = new SpriteEntity(SPRITE_SHAPE, SPRITE_TEXTURE_ATLAS);
        sprite.getTransformation()
                .translate(new Vector2f(800, 600));

        //Register input listeners for entity scale control
        Game.getInstance().getWindow().inputManager.getScrollCallback().register(e -> {
            float f = (float) -e.y;
            entity.getTransformation().scaleAdd(new Vector2f(10*f, 10*f));
        });

        //Register input listeners for sprite control
        Game.getInstance().getWindow().inputManager.getKeyCallback().register(e -> {
            //Increment segment on right arrow
            if(e.key == GLFW_KEY_RIGHT && e.action == Action.RELEASE){
                int after = (sprite.getSegment() + 1) % sprite.getTextureAtlas().segments;
                sprite.setSegment(after);
            }

            //Decrement segment on left arrow
            if(e.key == GLFW_KEY_LEFT && e.action == Action.RELEASE){
                int after = (sprite.getSegment() - 1) % sprite.getTextureAtlas().segments;
                if(after < 0) after += sprite.getTextureAtlas().segments;
                sprite.setSegment(after);
            }
        });
    }

    @Override
    public String getName() {
        return "Intro";
    }

    @Override
    protected void onEnter() {
        glClearColor(CLEAR_COLOR.getRed(), CLEAR_COLOR.getGreen(), CLEAR_COLOR.getBlue(), CLEAR_COLOR.getAlpha());
    }

    @Override
    protected void onUpdate() {
        //Update camera
        updateCamera();

        //Update entity transformations
        InputManager im = Game.getInstance().getWindow().inputManager;
        Vector2f d = new Vector2f();
        float z = 0;
        if(im.isMouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)){
            //Start at the mouse position (adjusted for camera position)
            d.set(im.getMousePosition());
            d.add(camera.getPosition());

            //Subtract the entity position
            Vector2f entityP = new Vector2f(
                    entity.getTransformation().getTranslation().x(),
                    entity.getTransformation().getTranslation().y());
            d.sub(entityP);
        }
        if(im.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
            z -= 90;
        }
        if(im.isMouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)){
            z += 90;
        }

        //Normalize and scale to speed
        if(d.x != 0 || d.y != 0){
            d.normalize();
            d.mul(ENTITY_SPEED * (float) Game.getInstance().delta.getDeltaSeconds());
            entity.getTransformation().translate(d);
        }

        //Rotate
        if(z != 0){
            entity.getTransformation().rotate((float) Math.toRadians(z * Game.getInstance().delta.getDeltaSeconds()));
        }

        //Update pulsating entity color
        pulsePhase += 2 * Game.getInstance().delta.getDeltaSeconds();
        pulsatingEntity.getColor().setRed((float) Math.abs(Math.sin(pulsePhase)));
    }

    /**
     * Updates the camera position based on WASD movement keys
     */
    protected void updateCamera(){
        Vector2f d = new Vector2f();
        if(Game.getInstance().getWindow().inputManager.isKeyDown(GLFW_KEY_W)){
            d.add(0, -1);
        }
        if(Game.getInstance().getWindow().inputManager.isKeyDown(GLFW_KEY_S)){
            d.add(0, 1);
        }
        if(Game.getInstance().getWindow().inputManager.isKeyDown(GLFW_KEY_A)){
            d.add(-1, 0);
        }
        if(Game.getInstance().getWindow().inputManager.isKeyDown(GLFW_KEY_D)){
            d.add(1, 0);
        }

        //Normalize and scale to speed
        if(d.x != 0 || d.y != 0){
            d.normalize();
            d.mul(CAMERA_SPEED * (float) Game.getInstance().delta.getDeltaSeconds());
            camera.move(d);
        }

        //Actually update the camera
        camera.update();
    }

    @Override
    protected void onRender() {
        staticEntity.render(camera);
        entity.render(camera);
        pulsatingEntity.render(camera);
        sprite.render(camera);
    }

    @Override
    public void onExit() {}

    @Override
    public void onCleanUp() {
        //Clean up entities
        entity.cleanUp();
        pulsatingEntity.cleanUp();
        staticEntity.cleanUp();
        sprite.cleanUp();

        //Clean up shapes
        SQUARE_SHAPE.cleanUp();
        BASIC_SQUARE.cleanUp();
        RAINBOW_SQUARE.cleanUp();
        SPRITE_SHAPE.cleanUp();

        //Clean up textures
        SPRITE_TEXTURE_ATLAS.cleanUp();
        TEST_TEXTURE.cleanUp();
    }
}
