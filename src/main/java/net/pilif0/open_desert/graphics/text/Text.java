package net.pilif0.open_desert.graphics.text;

import net.pilif0.open_desert.entities.Entity;
import net.pilif0.open_desert.geometry.Transformation;
import net.pilif0.open_desert.graphics.PerpendicularCamera;
import net.pilif0.open_desert.graphics.Renderable;
import net.pilif0.open_desert.graphics.ShaderProgram;
import net.pilif0.open_desert.graphics.Shaders;
import net.pilif0.open_desert.graphics.shapes.Shapes;
import org.joml.Vector2f;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * Represents a text.
 * The centre of mass of the text is at the top left corner of the whole text rectangle
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Text extends Entity implements Renderable{
    /** The content of this text */
    protected String content;
    /** The font texture atlas */
    protected Font font;
    /** The size of the font */
    protected int fontSize;
    /** Vector pointing down the height of one character */
    protected Vector2f stepDown;
    /** Vector pointing right the width of one character */
    protected Vector2f stepRight;
    /**
     * Constructs a text from its content, font size, and font
     *
     * @param content The content of the text
     * @param font The font (texture)
     * @param fontSize The size of the font
     */
    public Text(String content, Font font, int fontSize){
        this.content = content;
        this.fontSize = fontSize;
        stepDown = new Vector2f(0, fontSize);
        stepRight = new Vector2f(fontSize, 0);
        this.font = font;
    }

    @Override
    public void render(PerpendicularCamera camera, Transformation parentTransformation) {
        //Retrieve the shader
        ShaderProgram program = Shaders.get(ShaderProgram.SPRITE_SHADER);

        //Bind the shader
        program.bind();

        //Set the uniforms
        program.setUniform("projectionMatrix", camera.getMatrix());
        program.setUniform("parentMatrix", parentTransformation.getMatrix());
        program.setUniform("textureSampler", 0);

        //Bind the texture
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, font.ID);

        //Render each character
        int col = 0;
        Transformation pointer = new Transformation(getTransformation())
                .scaleMul(new Vector2f(fontSize))
                .translate(stepDown)
                .translate(stepRight.mul(0.5f, new Vector2f()));
        for(int i = 0; i < content.length(); i++){
            //Retrieve the character
            char c = content.charAt(i);

            //Handle new line
            if(c == '\n'){
                //Move the pointer col times to the left and one times down
                pointer.translate(stepRight.mul((-1) * col, new Vector2f()))
                    .translate(stepDown);

                //Reset col counter and increment row counter
                col = 0;
                continue;
            }

            //Set the texture segment
            program.setUniform("textureDelta", font.getDeltaCoordinates(getSegment(c)));

            //Set the world matrix (the text transformation plus character displacement)
            program.setUniform("worldMatrix", pointer.getMatrix());

            //Render the character
            Shapes.CHARACTER_SQUARE.render();

            //Increase the counters
            col++;
            pointer.translate(stepRight);
        }

        //Unbind the texture from the target
        glBindTexture(GL_TEXTURE_2D, 0);

        //Restore the shader
        program.unbind();
    }

    /**
     * Returns the number of the segment to use from the font texture atlas for the character
     *
     * @param c The character to seek
     * @return The number of the segment to use
     */
    public int getSegment(char c){
        //Convert to ASCII value
        return (int) c;
    }

    /**
     * Returns the content of the text
     *
     * @return The content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the text
     *
     * @param content The new value
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Returns the font of the text
     *
     * @return The font
     */
    public Font getFont() {
        return font;
    }

    /**
     * Sets the font of the text
     *
     * @param font The new value
     */
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * Returns the font size of the text
     *
     * @return The font size
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * Sets the font size of the text
     *
     * @param fontSize The new value
     */
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        stepDown = new Vector2f(0, fontSize);
        stepRight = new Vector2f(fontSize, 0);
    }
}
