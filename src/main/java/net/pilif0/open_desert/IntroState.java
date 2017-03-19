package net.pilif0.open_desert;

import net.pilif0.open_desert.graphics.Mesh;
import net.pilif0.open_desert.graphics.ShaderProgram;
import net.pilif0.open_desert.state.GameState;
import net.pilif0.open_desert.util.Color;
import net.pilif0.open_desert.util.Severity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
            -0.5f,  0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f
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

    /** The shader program being used */
    private ShaderProgram program;
    /** The mesh to draw */
    private Mesh mesh;

    public IntroState(){
        super();

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

    }

    @Override
    protected void onRender() {
        //Bind the shader
        program.bind();

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
