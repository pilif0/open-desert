package net.pilif0.open_desert.graphics;

import net.pilif0.open_desert.Launcher;
import net.pilif0.open_desert.util.Severity;

import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a shader program
 *
 * @author Filip Smola
 * @version 1.0
 */
public class ShaderProgram {
    /** The shader program ID */
    public final int programID;
    /** The vertex shader ID */
    private int vertexID;
    /** The fragment shader ID */
    private int fragmentID;

    /**
     * Constructs an empty program
     *
     * @throws GraphicsException On creation error
     */
    public ShaderProgram(){
        programID = glCreateProgram();

        if(programID == 0){
            throw new GraphicsException("Could not create shader program");
        }
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
    }

    /**
     * Binds the shader program
     */
    public void bind(){ glUseProgram(programID); }

    /**
     * Unbinds the shader program
     */
    public void unbind(){ glUseProgram(0); }

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
     * Creates a new shader and attaches it to this program
     *
     * @param code The code of the shader
     * @param type The type of the shader
     * @return The ID of the shader created
     * @throws GraphicsException On creation or compilation errors
     */
    protected int createShader(String code, int type) {
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

        //Attach and return
        glAttachShader(programID, shaderID);
        return shaderID;
    }
}
