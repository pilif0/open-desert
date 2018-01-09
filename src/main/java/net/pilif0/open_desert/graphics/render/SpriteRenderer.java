package net.pilif0.open_desert.graphics.render;

import net.pilif0.open_desert.Launcher;
import net.pilif0.open_desert.components.SpriteComponent;
import net.pilif0.open_desert.components.WorldMatrixComponent;
import net.pilif0.open_desert.ecs.GameObject;
import net.pilif0.open_desert.graphics.ShaderProgram;
import net.pilif0.open_desert.graphics.shapes.AbstractShape;
import net.pilif0.open_desert.graphics.shapes.Shape;
import org.joml.Matrix4fc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * Renders game objects onto the screen as sprites
 *
 * @author Filip Smola
 * @version 1.0
 */
public class SpriteRenderer{
    /** Dedicated shader for sprite rendering*/
    public static ShaderProgram SHADER;
    /** Dedicated shape for sprite rendering (unit square centered on origin) */
    public static AbstractShape SHAPE;

    static {
        // Initialise the shader
        SHADER = new ShaderProgram();
        try {
            String vertexCode = new String(Files.readAllBytes(Paths.get("shaders/component/Sprite.vs")));
            String fragmentCode = new String(Files.readAllBytes(Paths.get("shaders/component/Sprite.fs")));

            SHADER.attachVertexShader(vertexCode);
            SHADER.attachFragmentShader(fragmentCode);
            SHADER.link();
        } catch (IOException e) {
            Launcher.getLog().log("IO", e);
        }
        SHADER.createUniform("spriteDimensions");
        SHADER.createUniform("textureDimensions");
        SHADER.createUniform("projectionMatrix");
        SHADER.createUniform("worldMatrix");
        SHADER.createUniform("textureSampler");
        SHADER.createUniform("textureDelta");

        // Initialise the shape
        SHAPE = Shape.parse(Paths.get("shapes/component/Sprite.shape"));
    }

    public static void render(Matrix4fc projectionMatrix, GameObject go) {
        // Retrieve transformation from the appropriate components (position, rotation, scale)
        Matrix4fc worldMatrix = ((WorldMatrixComponent) go.getComponent("world_matrix")).getWorldMatrix();

        // Retrieve the sprite information component
        SpriteComponent spriteComponent = (SpriteComponent) go.getComponent("sprite");

        // Bind the shader
        SHADER.bind();

        // Set the uniforms
        SHADER.setUniform("spriteDimensions", spriteComponent.getDimensions());
        SHADER.setUniform("textureDimensions", spriteComponent.getAtlas().segmentSize);
        SHADER.setUniform("projectionMatrix", projectionMatrix);
        SHADER.setUniform("worldMatrix", worldMatrix);
        SHADER.setUniform("textureSampler", 0);
        SHADER.setUniform("textureDelta", spriteComponent.getTextureDelta());

        // Retrieve and bind the texture
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, spriteComponent.getTextureID());

        // Render the model (rectangle of certain width and height)
        SHAPE.render();

        // Unbind texture and program
        glBindTexture(GL_TEXTURE_2D, 0);
        ShaderProgram.unbind();
    }

    /**
     * Clean up after the renderer
     */
    public static void cleanUp() {
        SHAPE.cleanUp();
        SHADER.cleanUp();
    }
}
