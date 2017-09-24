package net.pilif0.open_desert.graphics;

import net.pilif0.open_desert.Launcher;
import net.pilif0.open_desert.util.Severity;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector4fc;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a shader program
 *
 * @author Filip Smola
 * @version 1.0
 */
public class ShaderProgram {
    /** The basic shader (expects only positions, colours in white) */
    public static final int BASIC_SHADER;
    /** The static color shader (expects positions and colours) */
    public static final int STATIC_COLOR_SHADER;
    /** The dynamic color shader (expects positions, colour as uniform) */
    public static final int DYNAMIC_COLOR_SHADER;
    /** The texture shader (expects positions and texture coordinates, texture sampler as uniform) */
    public static final int TEXTURE_SHADER;
    /** The sprite shader (expects positions and texture coordinates, texture sampler and segment number as uniform */
    public static final int SPRITE_SHADER;

    /** The shader program ID */
    private final int programID;
    /** The vertex shader ID */
    private int vertexID = 0;
    /** The fragment shader ID */
    private int fragmentID = 0;
    /** The uniforms of the shader program */
    private Map<String, Integer> uniforms;

    static{
        //Prepare temporary buffer variable
        ShaderProgram temp;

        //Create the basic shader
        temp = new ShaderProgram();
        try {
            String vertexCode = new String(Files.readAllBytes(Paths.get("shaders/vertex/Basic.vs")));
            String fragmentCode = new String(Files.readAllBytes(Paths.get("shaders/fragment/Basic.fs")));

            temp.attachVertexShader(vertexCode);
            temp.attachFragmentShader(fragmentCode);
            temp.link();
        } catch (IOException e) {
            Launcher.getLog().log("IO", e);
        }
        temp.createUniform("projectionMatrix");
        temp.createUniform("worldMatrix");
        temp.createUniform("parentMatrix");
        BASIC_SHADER = Shaders.add(temp);

        //Create the static color shader
        temp = new ShaderProgram();
        try {
            String vertexCode = new String(Files.readAllBytes(Paths.get("shaders/vertex/Color.vs")));
            String fragmentCode = new String(Files.readAllBytes(Paths.get("shaders/fragment/StaticColor.fs")));

            temp.attachVertexShader(vertexCode);
            temp.attachFragmentShader(fragmentCode);
            temp.link();
        } catch (IOException e) {
            Launcher.getLog().log("IO", e);
        }
        temp.createUniform("projectionMatrix");
        temp.createUniform("worldMatrix");
        temp.createUniform("parentMatrix");
        STATIC_COLOR_SHADER = Shaders.add(temp);

        //Create the dynamic color shader
        temp = new ShaderProgram();
        try {
            String vertexCode = new String(Files.readAllBytes(Paths.get("shaders/vertex/Basic.vs")));
            String fragmentCode = new String(Files.readAllBytes(Paths.get("shaders/fragment/DynamicColor.fs")));

            temp.attachVertexShader(vertexCode);
            temp.attachFragmentShader(fragmentCode);
            temp.link();
        } catch (IOException e) {
            Launcher.getLog().log("IO", e);
        }
        temp.createUniform("projectionMatrix");
        temp.createUniform("worldMatrix");
        temp.createUniform("parentMatrix");
        temp.createUniform("color");
        DYNAMIC_COLOR_SHADER = Shaders.add(temp);

        //Create the texture shader
        temp = new ShaderProgram();
        try {
            String vertexCode = new String(Files.readAllBytes(Paths.get("shaders/vertex/Texture.vs")));
            String fragmentCode = new String(Files.readAllBytes(Paths.get("shaders/fragment/Texture.fs")));

            temp.attachVertexShader(vertexCode);
            temp.attachFragmentShader(fragmentCode);
            temp.link();
        } catch (IOException e) {
            Launcher.getLog().log("IO", e);
        }
        temp.createUniform("projectionMatrix");
        temp.createUniform("worldMatrix");
        temp.createUniform("parentMatrix");
        temp.createUniform("textureSampler");
        TEXTURE_SHADER = Shaders.add(temp);

        //Create the sprite shader
        temp = new ShaderProgram();
        try {
            String vertexCode = new String(Files.readAllBytes(Paths.get("shaders/vertex/Texture.vs")));
            String fragmentCode = new String(Files.readAllBytes(Paths.get("shaders/fragment/TextureAtlas.fs")));

            temp.attachVertexShader(vertexCode);
            temp.attachFragmentShader(fragmentCode);
            temp.link();
        } catch (IOException e) {
            Launcher.getLog().log("IO", e);
        }
        temp.createUniform("projectionMatrix");
        temp.createUniform("worldMatrix");
        temp.createUniform("parentMatrix");
        temp.createUniform("textureSampler");
        temp.createUniform("textureDelta");
        SPRITE_SHADER = Shaders.add(temp);
    }

    /**
     * Constructs an empty program
     *
     * @throws GraphicsException On creation error
     */
    public ShaderProgram(){
        //Create the program
        programID = glCreateProgram();
        if(programID == 0){
            throw new GraphicsException("Could not create shader program");
        }

        //Initialise the uniforms map
        uniforms = new HashMap<>();
    }

    /**
     * Creates and attaches the vertex shader
     *
     * @param code The code of the shader
     */
    public void attachVertexShader(String code){
        //Detach the old shader if present
        if(vertexID != 0){
            glDetachShader(programID, vertexID);
        }

        //Attach the new shader
        vertexID = createShader(code, GL_VERTEX_SHADER);
        glAttachShader(programID, vertexID);
    }

    /**
     * Creates and attaches the fragment shader
     *
     * @param code The code of the shader
     */
    public void attachFragmentShader(String code){
        //Detach the old shader if present
        if(fragmentID != 0){
            glDetachShader(programID, fragmentID);
        }

        //Attach the new shader
        fragmentID = createShader(code, GL_FRAGMENT_SHADER);
        glAttachShader(programID, fragmentID);
    }

    /**
     * Links the shader program
     */
    public void link(){
        //Log any previous OpenGL error (to clear the flags)
        Launcher.getLog().logOpenGLError("OpenGL", "before shader link");

        //Link the program
        glLinkProgram(programID);

        //Check for OpenGL errors
        String programLog = glGetProgramInfoLog(programID);
        if (programLog.trim().length() > 0){
            Launcher.getLog().log(Severity.ERROR, "Shader Program", programLog);
        }

        //Check the program is linked
        if(glGetProgrami(programID, GL_LINK_STATUS) == 0){
            throw new GraphicsException("Error linking shader (" + glGetShaderInfoLog(programID) + ")");
        }

        //Validate the program
        glValidateProgram(programID);

        //Check the program is validated
        if(glGetProgrami(programID, GL_VALIDATE_STATUS) == 0){
            Launcher.getLog().log(Severity.WARNING, "Shader program", "Warning validating shader (" + glGetShaderInfoLog(programID) + ")");
        }

        //Log OpenGL errors
        Launcher.getLog().logOpenGLError("ShaderProgram", "when linking");
    }

    /**
     * Binds the shader program
     */
    public void bind(){ glUseProgram(programID); }

    /**
     * Unbinds the shader program
     */
    public static void unbind(){ glUseProgram(0); }

    /**
     * Cleans up after the program (unbinds, detaches and deletes)
     */
    public void cleanUp(){
        //Make sure the program is not bound
        unbind();

        if(programID != 0){
            //Detach vertex shader
            if(vertexID != 0){
                glDetachShader(programID, vertexID);
            }

            //Detach fragment shader
            if(fragmentID != 0){
                glDetachShader(programID, fragmentID);
            }

            //Delete the program
            glDeleteProgram(programID);
        }
    }

    /**
     * Creates a uniform entry in the program
     *
     * @param name The name of the uniform
     */
    public void createUniform(String name){
        //Get the uniform location
        int location = glGetUniformLocation(programID, name);

        //Verify location
        if(location < 0){
            throw new GraphicsException("Could not find uniform " + name);
        }

        //Add the uniform to the map
        uniforms.put(name, location);
    }

    /**
     * Sets the uniform value
     *
     * @param name The uniform name
     * @param value The uniform value
     */
    public void setUniform(String name, Matrix4fc value){
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.mallocFloat(16);
            value.get(buffer);
            glUniformMatrix4fv(uniforms.get(name), false, buffer);
        }
    }

    /**
     * Sets the uniform value
     *
     * @param name The uniform name
     * @param value The uniform value
     */
    public void setUniform(String name, Vector4fc value){
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.mallocFloat(4);
            value.get(buffer);
            glUniform4fv(uniforms.get(name), buffer);
        }
    }

    /**
     * Sets the uniform value
     *
     * @param name The uniform name
     * @param value The uniform value
     */
    public void setUniform(String name, int value){
        try(MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer buffer = stack.mallocInt(1);
            buffer.put(value).flip();
            glUniform1iv(uniforms.get(name), buffer);
        }
    }

    /**
     * Sets the uniform value
     *
     * @param name The uniform name
     * @param value The uniform value
     */
    public void setUniform(String name, Vector2fc value) {
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.mallocFloat(2);
            value.get(buffer);
            glUniform2fv(uniforms.get(name), buffer);
        }
    }

    /**
     * Creates a new shader and attaches it to this program
     *
     * @param code The code of the shader
     * @param type The type of the shader
     * @return The ID of the shader created
     * @throws GraphicsException On creation or compilation errors
     */
    private static int createShader(String code, int type) {
        //Create the shader
        int shaderID = glCreateShader(type);
        if (shaderID == 0) {
            throw new GraphicsException("Could not create new shader");
        }

        //Compile code
        glShaderSource(shaderID, code);
        glCompileShader(shaderID);

        //Verify
        String shaderLog = glGetShaderInfoLog(shaderID);
        if (shaderLog.trim().length() > 0) {
            Launcher.getLog().log(Severity.ERROR, "Shader creation", shaderLog);
        }

        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0) {
            throw new GraphicsException("Could not compile a new shader. Code: " + code);
        }

        return shaderID;
    }
}
