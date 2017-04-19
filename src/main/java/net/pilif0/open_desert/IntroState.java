package net.pilif0.open_desert;

import net.pilif0.open_desert.graphics.Mesh;
import net.pilif0.open_desert.graphics.PerpendicularCamera;
import net.pilif0.open_desert.graphics.Vertex;
import net.pilif0.open_desert.input.InputManager;
import net.pilif0.open_desert.state.GameState;
import net.pilif0.open_desert.util.Color;
import org.joml.*;
import org.joml.Math;

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
    /** The vertices of the square */
    public static final Vertex[] VERTICES;
    /** The indices to use when drawing */
    public static final int[] INDICES = new int[]{0, 1, 3, 3, 1, 2};
    /** The camera movement speed */
    public static final float CAMERA_SPEED = 750f;
    /** The entity movement speed */
    public static final float ENTITY_SPEED = 250f;
    /** The square mesh */
    public static final Mesh SQUARE_MESH;

    static{
        Vertex.VertexBuilder vb = new Vertex.VertexBuilder(
                new Vector3f(-0.5f, 0.5f, 0f),
                new Color(0xff_ff_ff_ff)
        );

        VERTICES = new Vertex[4];
        VERTICES[0] = vb.build();
        vb.position.add(0, -1f, 0);
        VERTICES[1] = vb.build();
        vb.position.add(1f, 0, 0);
        VERTICES[2] = vb.build();
        vb.position.add(0, 1f, 0);
        VERTICES[3] = vb.build();

        SQUARE_MESH = new Mesh(VERTICES, INDICES);
    }

    /** The entity to draw */
    private ColoredEntity entity;
    /** The camera */
    private PerpendicularCamera camera;
    /** The entity colour animation timer */
    private double colourTimer;

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
        entity = new ColoredEntity(SQUARE_MESH);
        entity.getTransformation().setScale(new Vector3f(100, 100, 100));

        //Register input listeners for entity scale control
        Game.getInstance().getWindow().inputManager.getScrollCallback().register(e -> {
            float f = (float) -e.y;
            entity.getTransformation().scaleAdd(new Vector3f(10*f, 10*f, 10*f));
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
            entity.getTransformation().translate(new Vector3f(d, 0));
        }

        //Rotate
        if(z != 0){
            entity.getTransformation().rotate(new Vector3f(0, 0, (float) Math.toRadians(z * (float) Game.getInstance().delta.getDeltaSeconds())));
        }

        //Update entity colour
        colourTimer += Game.getInstance().delta.getDeltaSeconds();
        float red = ((float) Math.sin(colourTimer) + 1) / 2;
        float green = ((float) Math.sin(colourTimer * 0.5) + 1) / 2;
        float blue = ((float) Math.sin(colourTimer * 0.25) + 1) / 2;
        entity.getColor().setRed(red);
        entity.getColor().setGreen(green);
        entity.getColor().setBlue(blue);
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
