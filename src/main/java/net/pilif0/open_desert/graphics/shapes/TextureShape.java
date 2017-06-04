package net.pilif0.open_desert.graphics.shapes;

import net.pilif0.open_desert.Launcher;
import net.pilif0.open_desert.graphics.vertices.TextureVertex;
import org.lwjgl.system.MemoryStack;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * Represents a general textured shape in two dimensions
 * Attributes: 0 - 2D position, 1 - texture coordinate
 *
 * @author Filip Smola
 * @version 1.0
 */
public class TextureShape extends AbstractShape{
    //Parser XPath expressions
    private static final XPathExpression typeExpr;
    private static final XPathExpression indexExpr;
    private static final XPathExpression posXExpr;
    private static final XPathExpression posYExpr;
    private static final XPathExpression texXExpr;
    private static final XPathExpression texYExpr;
    private static final XPathExpression indexNodesExpr;

    static{
        //Prepare to use XPath
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();

        //Compile the XPath expressions
        try {
            typeExpr = xpath.compile("/shape/@type");
            indexExpr = xpath.compile("//vertex/@id");
            posXExpr = xpath.compile("//vertex/position/x");
            posYExpr = xpath.compile("//vertex/position/y");
            texXExpr = xpath.compile("//vertex/texture/x");
            texYExpr = xpath.compile("//vertex/texture/y");
            indexNodesExpr = xpath.compile("/shape/indices/index/text()");
        }catch(XPathExpressionException e){
            //When XPath compilation fails, there is no hope. Just die.
            throw new RuntimeException("TextureShape XPath compilation failed");
        }
    }

    /**
     * Constructs the shape
     *
     * @param vertices The vertices to use
     * @param indices The indices to use
     */
    public TextureShape(TextureVertex[] vertices, int[] indices){
        //Log any previous OpenGL error (to clear the flags)
        Launcher.getLog().logOpenGLError("OpenGL", "before shape creation");

        //Count vertices
        vertexCount = indices.length;

        //Prepare VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        //Vertices
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer verticesBuffer = stack.mallocFloat(vertices.length * 4);
            for(int i = 0; i < vertices.length; i++){
                verticesBuffer.put(vertices[i].toInterleaved());
            }
            verticesBuffer.flip();

            vboID = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * 4, 0);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * 4, 2 * 4);
        }

        //Indices
        try(MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer indicesBuffer = stack.mallocInt(indices.length);
            indicesBuffer.put(indices).flip();

            idxVboID = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboID);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        }

        //Unbind
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        //Log OpenGL errors
        Launcher.getLog().logOpenGLError("TextureShape", "when creating");
    }

    /**
     * Cleans up the shape
     */
    public void cleanUp(){
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vboID);
        glDeleteBuffers(idxVboID);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoID);
    }

    /**
     * Parses a shape file of TextureShape type into a TextureShape object
     *
     * @param p The path to the shape file
     * @return The parsed object
     * @throws ShapeParseException When object is in wrong format
     */
    public static TextureShape parse(Path p){
        //Parse the shape file (XML)
        Document doc;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(p.toFile());
        }catch(Exception e){
            throw new ShapeParseException(p, e);
        }

        //Verify the type
        String type;
        try {
            type = (String) typeExpr.evaluate(doc, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            //Assume wrong type when evaluation fails
            type = "wrong";
        }
        if(!type.equals("TextureShape")){
            throw new ShapeParseException(p, "Wrong type");
        }

        //Gather all vertex nodes
        List<TextureVertex> vertexList = new ArrayList<>();
        try{
            NodeList indexNodes = (NodeList) indexExpr.evaluate(doc, XPathConstants.NODESET);
            NodeList xNodes = (NodeList) posXExpr.evaluate(doc, XPathConstants.NODESET);
            NodeList yNodes = (NodeList) posYExpr.evaluate(doc, XPathConstants.NODESET);
            NodeList texXNodes = (NodeList) texXExpr.evaluate(doc, XPathConstants.NODESET);
            NodeList texYNodes = (NodeList) texYExpr.evaluate(doc, XPathConstants.NODESET);

            for(int i = 0; i < indexNodes.getLength(); i++){
                int index = Integer.parseInt(indexNodes.item(i).getTextContent());
                float x = Float.parseFloat(xNodes.item(i).getTextContent());
                float y = Float.parseFloat(yNodes.item(i).getTextContent());
                float tX = Float.parseFloat(texXNodes.item(i).getTextContent());
                float tY = Float.parseFloat(texYNodes.item(i).getTextContent());

                TextureVertex vertex = new TextureVertex(x, y, tX, tY);
                vertexList.add(index, vertex);
            }
        }catch(XPathExpressionException e){
            throw new ShapeParseException(p, e);
        }
        TextureVertex[] vertices = vertexList.toArray(new TextureVertex[vertexList.size()]);

        //Gather the indices
        List<Integer> indexList = new ArrayList<>();
        try{
            NodeList indexNodes = (NodeList) indexNodesExpr.evaluate(doc, XPathConstants.NODESET);
            for(int i = 0; i < indexNodes.getLength(); i++){
                indexList.add(i, Integer.parseInt(indexNodes.item(i).getTextContent()));
            }
        }catch(XPathExpressionException e){
            throw new ShapeParseException(p, e);
        }
        int[] indices = indexList.stream().mapToInt(Integer::intValue).toArray();

        //Put the shape together and return it
        return new TextureShape(vertices, indices);
    }
}
