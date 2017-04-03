package net.pilif0.open_desert;

import net.pilif0.open_desert.graphics.Mesh;
import net.pilif0.open_desert.graphics.ShaderProgram;
import net.pilif0.open_desert.graphics.TopDownCamera;
import net.pilif0.open_desert.graphics.Vertex;
import net.pilif0.open_desert.input.Action;
import net.pilif0.open_desert.state.GameState;
import net.pilif0.open_desert.util.Color;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

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
    public static final float CAMERA_SPEED = 10f;
    /** The entity movement speed */
    public static final float ENTITY_SPEED = 10f;
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

    /** The shader program being used */
    private ShaderProgram program;
    /** The entity to draw */
    private Entity entity;
    /** The camera */
    private TopDownCamera camera;

    /**
     * Constructs the state
     */
    public IntroState(){
        super();

        //Create the camera
        camera = new TopDownCamera(new Vector2f(0, 0), Game.getInstance().getWindow().getResolution());

        //Have the camera listen to window resolution change
        Game.getInstance().getWindow().resolutionMultiplexer.register(e -> {
            camera.setDimensions(new Vector2i(e.newX, e.newY));
        });

        //Create the shader program
        try {
            String vertexCode = new String(Files.readAllBytes(Paths.get("shaders/vertex.vs")));
            String fragmentCode = new String(Files.readAllBytes(Paths.get("shaders/fragment.fs")));

            program = new ShaderProgram();
            program.attachVertexShader(vertexCode);
            program.attachFragmentShader(fragmentCode);
            program.link();
        } catch (IOException e) {
            Launcher.getLog().log("IO", e);
        }

        //Create the matrices uniforms
        program.createUniform("projectionMatrix");
        program.createUniform("worldMatrix");

        //Create the entity and scale it by factor of 100
        entity = new Entity(SQUARE_MESH);
        entity.getTransformation().setScale(new Vector3f(100, 100, 100));

        //Register input listeners for entity scale control
        Game.getInstance().getWindow().inputManager.getEventMultiplexer().register(e -> {
            if(e.key == GLFW_KEY_KP_ADD && e.action == Action.PRESS){
                entity.getTransformation().scaleAdd(new Vector3f(10, 10, 10));
            }

            if(e.key == GLFW_KEY_KP_SUBTRACT && e.action == Action.PRESS){
                entity.getTransformation().scaleAdd(new Vector3f(-10, -10, -10));
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

        //Update entity
        Vector2f d = new Vector2f();
        float z = 0;
        if(Game.getInstance().getWindow().inputManager.isKeyDown(GLFW_KEY_UP)){
            d.add(0, -1);
        }
        if(Game.getInstance().getWindow().inputManager.isKeyDown(GLFW_KEY_DOWN)){
            d.add(0, 1);
        }
        if(Game.getInstance().getWindow().inputManager.isKeyDown(GLFW_KEY_LEFT)){
            d.add(-1, 0);
        }
        if(Game.getInstance().getWindow().inputManager.isKeyDown(GLFW_KEY_RIGHT)){
            d.add(1, 0);
        }
        if(Game.getInstance().getWindow().inputManager.isKeyDown(GLFW_KEY_J)){
            z -= 10;
        }
        if(Game.getInstance().getWindow().inputManager.isKeyDown(GLFW_KEY_K)){
            z += 10;
        }

        //Normalize and scale to speed
        if(d.x != 0 || d.y != 0){
            d.normalize();
            d.mul(ENTITY_SPEED);
            entity.getTransformation().translate(new Vector3f(d, 0));
        }

        //Rotate
        if(z != 0){
            entity.getTransformation().rotate(new Vector3f(0, 0, (float) Math.toRadians(z)));
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
            d.mul(CAMERA_SPEED);
            camera.move(d);
        }

        //Actually update the camera
        camera.update();
    }

    @Override
    protected void onRender() {
        //Bind the shader
        program.bind();

        //Set the uniforms
        program.setUniform("projectionMatrix", camera.getMatrix());
        program.setUniform("worldMatrix", entity.getTransformation().getMatrix());

        //Bind the VAO
        glBindVertexArray(entity.getMesh().vaoID);

        //Draw
        glDrawElements(GL_TRIANGLES, entity.getMesh().vertexCount, GL_UNSIGNED_INT, 0);

        //Restore
        glBindVertexArray(0);
        program.unbind();
    }

    @Override
    public void onExit() {

    }

    @Override
    public void onCleanUp() {
        //Clean up shader
        if(program != null){
            program.cleanUp();
        }

        //Clean up mesh
        entity.cleanUp();
    }
}
