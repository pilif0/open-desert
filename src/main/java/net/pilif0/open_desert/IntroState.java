package net.pilif0.open_desert;

import net.pilif0.open_desert.ecs.Components;
import net.pilif0.open_desert.ecs.GameObject;
import net.pilif0.open_desert.ecs.Template;
import net.pilif0.open_desert.entities.ColorEntity;
import net.pilif0.open_desert.entities.DynamicColorEntity;
import net.pilif0.open_desert.geometry.Transformation;
import net.pilif0.open_desert.graphics.*;
import net.pilif0.open_desert.graphics.render.SpriteRenderer;
import net.pilif0.open_desert.graphics.shapes.ColorShape;
import net.pilif0.open_desert.graphics.shapes.Shape;
import net.pilif0.open_desert.graphics.text.Font;
import net.pilif0.open_desert.graphics.text.Text;
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
    /** The entity movement speed */
    public static final float ENTITY_SPEED = 250f;
    /** The rainbow square shape */
    public static final ColorShape RAINBOW_SQUARE;
    /** The basic square shape */
    public static final Shape BASIC_SQUARE;
    /** The text font */
    public static final Font TEXT_FONT;

    static{
        //Parse the shapes
        RAINBOW_SQUARE = ColorShape.parse(Paths.get("shapes/RainbowSquare.shape"));
        BASIC_SQUARE = Shape.parse(Paths.get("shapes/Square.shape"));

        //Read the font
        try{
            TEXT_FONT = new Font(Paths.get("textures/font.png"));
        }catch(IOException e){
            Launcher.getLog().log("Texture", e);
            throw new RuntimeException("Crash because of font");
        }
    }

    /** The static entity */
    private ColorEntity staticEntity;
    /** The pulsating entity */
    private DynamicColorEntity pulsatingEntity;
    /** The camera */
    private PerpendicularCamera camera;
    /** The text */
    private Text text;

    /** Static sprite square */
    private GameObject spriteGO;
    /** Texture test square */
    private GameObject textureGO;
    /** Object for the camera to follow */
    private GameObject cameraFocus;

    /**
     * Constructs the state
     */
    public IntroState(){
        super();

        // Load declared components
        try {
            Components.from(Paths.get("main.components"));
        } catch (IOException e) {
            Launcher.getLog().log("Main Components File", e);
            System.exit(1);
        }
        try {
            Components.from(Paths.get("control.components"));
        } catch (IOException e) {
            Launcher.getLog().log("Control Components File", e);
            System.exit(1);
        }

        //Create the camera
        camera = new PerpendicularCamera(new Vector2f(0, 0), Game.getInstance().getWindow().getResolution());

        //Have the camera listen to window resolution change
        Game.getInstance().getWindow().resolutionMultiplexer.register(e -> camera.setDimensions(new Vector2i(e.newX, e.newY)));

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
        pulsatingEntity.addDirector(new DynamicColorEntity.DynamicColorEntityDirector(pulsatingEntity) {
            /** The pulse phase */
            private double pulsePhase = 0;

            @Override
            public void update() {
                pulsePhase += 2 * Game.getInstance().delta.getDeltaSeconds();
                entity.getColor().setRed((float) Math.abs(Math.sin(pulsePhase)));
            }
        });

        // Create the sprite square
        try {
            spriteGO = new GameObject(new Template(Paths.get("templates/numbers.template")));
        } catch (IOException e) {
            Launcher.getLog().log("spriteGO", e);
            System.exit(1);
        }

        // Create the texture square
        try {
            textureGO = new GameObject(new Template(Paths.get("templates/texture_test.template")));
        } catch (IOException e) {
            Launcher.getLog().log("textureGO", e);
            System.exit(1);
        }

        // Create the camera focus
        try {
            cameraFocus = new GameObject(new Template(Paths.get("templates/camera_focus.template")));
        } catch (IOException e) {
            Launcher.getLog().log("cameraFocus", e);
            System.exit(1);
        }

        //Create the text
        text = new Text("Hello world!\nTest of new line", TEXT_FONT, 32);
        text.getTransformation()
                .translate(new Vector2f(-400, -400));
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
            Vector2fc entityP = textureGO.position.getPosition();
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
            textureGO.position.addPosition(d);
        }

        //Rotate
        if(z != 0){
            textureGO.rotation.addRotation((float) Math.toRadians(z * Game.getInstance().delta.getDeltaSeconds()));
        }

        //Update entities
        pulsatingEntity.update();

        // Update GOs
        cameraFocus.update(Game.getInstance().delta.getDelta());
        textureGO.update(Game.getInstance().delta.getDelta());
        spriteGO.update(Game.getInstance().delta.getDelta());
    }

    /**
     * Updates the camera position based on WASD movement keys
     */
    protected void updateCamera(){
        // Move the camera to follow the focus
        camera.setPosition(cameraFocus.position.getPosition());

        //Actually update the camera
        camera.update();
    }

    @Override
    protected void onRender() {
        staticEntity.render(camera);
        SpriteRenderer.render(camera.getMatrix(), textureGO);
        pulsatingEntity.render(camera);
        SpriteRenderer.render(camera.getMatrix(), spriteGO);
        text.render(camera);
    }

    @Override
    public void onExit() {}

    @Override
    public void onCleanUp() {
        //Clean up entities
        textureGO.cleanUp();
        pulsatingEntity.cleanUp();
        staticEntity.cleanUp();
        spriteGO.cleanUp();
        text.cleanUp();
        cameraFocus.cleanUp();

        //Clean up shapes
        BASIC_SQUARE.cleanUp();
        RAINBOW_SQUARE.cleanUp();

        //Clean up textures
        TextureAtlas.cleanAll();

        // Clean up renderer
        SpriteRenderer.cleanUp();
    }
}
