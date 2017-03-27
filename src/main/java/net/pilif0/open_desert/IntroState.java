package net.pilif0.open_desert;

import net.pilif0.open_desert.graphics.Mesh;
import net.pilif0.open_desert.graphics.ShaderProgram;
import net.pilif0.open_desert.graphics.TopDownCamera;
import net.pilif0.open_desert.state.GameState;
import net.pilif0.open_desert.util.Color;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    /** The vertices to draw */
    public static final float[] VERTICES = new float[]{
            100f,  500f, 0f,
            100f, 100f, 0f,
            500f, 100f, 0f,
            500f, 500f, 0f
    };
    /** The colours of the vertices */
    public static final float[] COLORS = new float[]{
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 1.0f
    };
    /** The indices to use when drawing */
    public static final int[] INDICES = new int[]{0, 1, 3, 3, 1, 2};
    /** The camera movement speed */
    public static final float CAMERA_SPEED = 10f;

    /** The shader program being used */
    private ShaderProgram program;
    /** The mesh to draw */
    private Mesh mesh;
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

        //Create the projection matrix uniform
        program.createUniform("projectionMatrix");

        //Create the mesh
        mesh = new Mesh(VERTICES, COLORS, INDICES);
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
    }

    @Override
    protected void onRender() {
        //Bind the shader
        program.bind();

        //Set the uniform
        program.setUniform("projectionMatrix", camera.getMatrix());

        //Bind the VAO
        glBindVertexArray(mesh.vaoID);

        //Draw
        glDrawElements(GL_TRIANGLES, mesh.vertexCount, GL_UNSIGNED_INT, 0);

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
        mesh.cleanUp();
    }
}
