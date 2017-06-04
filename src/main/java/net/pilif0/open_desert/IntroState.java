package net.pilif0.open_desert;

import net.pilif0.open_desert.entities.TextureEntity;
import net.pilif0.open_desert.graphics.*;
import net.pilif0.open_desert.graphics.shapes.TextureShape;
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
    /** The square shape */
    public static final TextureShape SQUARE_SHAPE;
    /** The test texture */
    public static final Texture TEST_TEXTURE;

    static{
        //Parse the shape
        SQUARE_SHAPE = TextureShape.parse(Paths.get("shapes/TexturedSquare.shape"));

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
        entity.getTransformation().setScale(new Vector2f(100, 100));

        //Register input listeners for entity scale control
        Game.getInstance().getWindow().inputManager.getScrollCallback().register(e -> {
            float f = (float) -e.y;
            entity.getTransformation().scaleAdd(new Vector2f(10*f, 10*f));
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
        entity.render(camera);
    }

    @Override
    public void onExit() {}

    @Override
    public void onCleanUp() {
        //Clean up entity
        entity.cleanUp();
    }
}
