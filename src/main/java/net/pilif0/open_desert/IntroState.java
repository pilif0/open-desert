package net.pilif0.open_desert;

import net.pilif0.open_desert.components.SpriteComponent;
import net.pilif0.open_desert.ecs.Components;
import net.pilif0.open_desert.ecs.GameObject;
import net.pilif0.open_desert.ecs.Template;
import net.pilif0.open_desert.entities.ColorEntity;
import net.pilif0.open_desert.entities.DynamicColorEntity;
import net.pilif0.open_desert.entities.TextureEntity;
import net.pilif0.open_desert.geometry.Transformation;
import net.pilif0.open_desert.graphics.*;
import net.pilif0.open_desert.graphics.render.SpriteRenderer;
import net.pilif0.open_desert.graphics.shapes.ColorShape;
import net.pilif0.open_desert.graphics.shapes.Shape;
import net.pilif0.open_desert.graphics.shapes.SpriteShape;
import net.pilif0.open_desert.graphics.shapes.TextureShape;
import net.pilif0.open_desert.graphics.text.Font;
import net.pilif0.open_desert.graphics.text.Text;
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
    public static final PNGTexture TEST_TEXTURE;
    /** The text font */
    public static final Font TEXT_FONT;

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
            TEST_TEXTURE = new PNGTexture(Paths.get("textures/test.png"));
        } catch (IOException e) {
            Launcher.getLog().log("Texture", e);
            throw new RuntimeException("Crash because of texture");
        }

        //Read the font
        try{
            TEXT_FONT = new Font(Paths.get("textures/font.png"));
        }catch(IOException e){
            Launcher.getLog().log("Texture", e);
            throw new RuntimeException("Crash because of font");
        }
    }

    /** The entity to draw */
    private TextureEntity entity;
    /** The static entity */
    private ColorEntity staticEntity;
    /** The pulsating entity */
    private DynamicColorEntity pulsatingEntity;
    /** The camera */
    private PerpendicularCamera camera;
    /** The text */
    private Text text;

    /** The static square */
    private GameObject spriteGO;

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
        pulsatingEntity.addDirector(new DynamicColorEntity.DynamicColorEntityDirector(pulsatingEntity) {
            /** The pulse phase */
            private double pulsePhase = 0;

            @Override
            public void update() {
                pulsePhase += 2 * Game.getInstance().delta.getDeltaSeconds();
                entity.getColor().setRed((float) Math.abs(Math.sin(pulsePhase)));
            }
        });

        // Create the sprite entity
        try {
            spriteGO = new GameObject(new Template(Paths.get("templates/numbers.template")));
        } catch (IOException e) {
            Launcher.getLog().log("spriteGO", e);
            System.exit(1);
        }

        //Create the text
        text = new Text("Hello world!\nTest of new line", TEXT_FONT, 32);
        text.getTransformation()
                .translate(new Vector2f(-400, -400));

        //Register input listeners for entity scale control
        Game.getInstance().getWindow().inputManager.getScrollCallback().register(e -> {
            float f = (float) -e.y;
            entity.getTransformation().scaleAdd(new Vector2f(10*f, 10*f));
        });

        // Register input listeners for sprite control
        Game.getInstance().getWindow().inputManager.getKeyCallback().register(e -> {
            SpriteComponent spriteComponent = (SpriteComponent) spriteGO.getComponent("sprite");

            // Increment segment on right arrow
            if(e.key == GLFW_KEY_RIGHT && e.action == Action.RELEASE){
                int after = (spriteComponent.getIndex() + 1) % spriteComponent.getAtlas().segments;
                spriteComponent.setIndex(after);
            }

            // Decrement segment on left arrow
            if(e.key == GLFW_KEY_LEFT && e.action == Action.RELEASE){
                int after = (spriteComponent.getIndex() - 1) % spriteComponent.getAtlas().segments;
                if(after < 0) after += spriteComponent.getAtlas().segments;
                spriteComponent.setIndex(after);
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

        //Update entities
        pulsatingEntity.update();
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
        SpriteRenderer.render(camera.getMatrix(), spriteGO);
        text.render(camera);
    }

    @Override
    public void onExit() {}

    @Override
    public void onCleanUp() {
        //Clean up entities
        entity.cleanUp();
        pulsatingEntity.cleanUp();
        staticEntity.cleanUp();
        spriteGO.cleanUp();
        text.cleanUp();

        //Clean up shapes
        SQUARE_SHAPE.cleanUp();
        BASIC_SQUARE.cleanUp();
        RAINBOW_SQUARE.cleanUp();
        SPRITE_SHAPE.cleanUp();

        //Clean up textures
        SPRITE_TEXTURE_ATLAS.cleanUp();
        TEST_TEXTURE.cleanUp();

        // Clean up renderer
        SpriteRenderer.cleanUp();
    }

    /**
     * Cleans up after the entity
     */
    public void cleanUp(){}
}
