package net.pilif0.open_desert.world;

import net.pilif0.open_desert.Launcher;
import net.pilif0.open_desert.ecs.GameObject;
import net.pilif0.open_desert.util.Severity;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.Iterator;
import java.util.List;

/**
 * Quad tree representation of the (2D) world utilising the position component of game objects.
 * The borders between quads belong to the "more positive" side - the one closer to the point (+infty,+infty).
 * The world is assumed to be square.
 *
 * @author Filip Smola
 * @version 1.0
 */
// Yggdrasil
public class WorldTree {
    /** Root of the tree */
    public final Quad root;

    /**
     * Construct a world tree for a square world
     *
     * @param a Length of one side of the world
     */
    public WorldTree(float a){
        float h = a / 2;
        Vector2fc min = (new Vector2f(-h, -h)).toImmutable();
        Vector2fc max = (new Vector2f(h, h)).toImmutable();
        root = new Quad(min, max);
    }

    /**
     * Construct a world tree for a rectangle world
     *
     * @param min Corner with minimal coordinates
     * @param max Corner with maximal coordinates
     */
    public WorldTree(Vector2fc min, Vector2fc max){
        root = new Quad(min, max);
    }

    /**
     * Get the leaf quad that contains the specified position
     *
     * @param p Position to look for
     * @return Leaf quad that contains the position
     */
    public Quad getLeafAt(Vector2fc p){
        Quad curr = root;

        while(!curr.isLeaf()){
            curr = curr.getChild(p);
        }

        return curr;
    }

    /**
     * Represents a single node in the quad tree.
     *
     * Includes its minimal borders.
     */
    public static class Quad{
        /** The maximum number of game objects in the quad before it splits */
        public static final int CONTENT_LIMIT = 1024;

        /** Corner of the quad with maximal x and y coordinates */
        private final Vector2fc max;
        /** Corner of the quad with minimal x and y coordinates */
        private final Vector2fc min;
        /** Center of the quad */
        private Vector2fc mid;
        /** Children of this quad - either {@code null} when this quad is a leaf, or exactly four quads (from top left clockwise) */
        private Quad[] children;
        /** Game objects in this quad */
        private List<GameObject> contents;
        // Possibly also buffer the side length (if it becomes accessed often enough)

        /**
         * Construct a quad from its corners
         *
         * @param min Corner with minimal coordinates
         * @param max Corner with maximal coordinates
         */
        private Quad(Vector2fc min, Vector2fc max){
            this.min = min;
            this.max = max;
            this.mid = (new Vector2f(min)).add(max).mul(0.5f).toImmutable();
        }

        /**
         * Split the quad into four quads.
         */
        //Should probably be synchronised to avoid operations on partly split quads
        private void split(){
            // Skip if already split
            if(children != null){
                return;
            }

            // Prepare values
            children = new Quad[4];
            float halfSide = getSide() / 2;
            Vector2fc leftMid   = (new Vector2f(mid)).sub(halfSide, 0).toImmutable();
            Vector2fc topMid    = (new Vector2f(max)).sub(halfSide, 0).toImmutable();
            Vector2fc rightMid  = (new Vector2f(mid)).add(halfSide, 0).toImmutable();
            Vector2fc botMid    = (new Vector2f(min)).add(halfSide, 0).toImmutable();

            // Fill the children
            children[0] = new Quad(leftMid, topMid);    // Top-left
            children[1] = new Quad(mid, max);           // Top-right
            children[2] = new Quad(botMid, rightMid);   // Bot-right
            children[3] = new Quad(min, mid);           // Bot-left

            // Transfer contents
            Iterator<GameObject> it = contents.iterator();
            while(it.hasNext()){
                GameObject o = it.next();

                // Move the game object
                it.remove();
                getChild(o.position.getPosition()).add(o, false);  // Position already checked
            }
        }

        /**
         * Add a game object to the quad.
         * Checks whether the game object has a valid position (is in the quad).
         *
         * @param o Game object to add
         */
        public void add(GameObject o){
            add(o, true);
        }

        /**
         * Add a game object to the quad.
         *
         * @param o Game object to add
         * @param check Whether to check the game object's position
         */
        private void add(GameObject o, boolean check){
            // Check position only when asked to
            if(check && !contains(o.position.getPosition())){
                Launcher.getLog().log(
                        Severity.ERROR,
                        "Quad.add()",
                        String.format("The game object (%s) cannot be added to the quad {%s, %s} because the quad does not contain it.", o.handle, min, max)
                );
            }

            // Handle leaf quad
            if(isLeaf()){
                // Check if there is space
                if(contents.size() <= CONTENT_LIMIT){
                    contents.add(o);
                    return;
                }else{
                    split();
                }
            }

            // Handle inner quad
            getChild(o.position.getPosition()).add(o, false);  // Position already checked
        }

        /**
         * Calculate the index of the child that contains the provided position
         *
         * @param p Position
         * @return Index of the child
         */
        //TODO handle leaf?
        private int getChildIndex(Vector2fc p){
            if(p.x() >= mid.x()){
                // Right
                if(p.y() >= mid.y()){
                    // Top-right
                    return 1;
                }else{
                    // Bot-right
                    return 2;
                }
            }else{
                // Left
                if(p.y() >= mid.y()){
                    // Top-left
                    return 0;
                }else{
                    // Bot-left
                    return 3;
                }
            }
        }

        /**
         * Get the child quad that contains the provided position
         *
         * @param p Position
         * @return Child quad that contain the position
         */
        //TODO handle leaf?
        private Quad getChild(Vector2fc p){
            return children[getChildIndex(p)];
        }

        /**
         * Whether this quad contains the point
         *
         * @param p Point to check
         * @return Whether this point is contained
         */
        public boolean contains(Vector2fc p){
            if(p.x() >= max.x() || p.y() >= max.y()){
                return false;
            }
            if(p.x() < min.x() || p.y() < min.y()){
                return false;
            }
            return true;
        }

        /**
         * Get the length of one side of the quad
         *
         * @return Length of one side
         */
        public float getSide(){
            return max.x() - min.x();
        }

        /**
         * Whether this quad is a leaf quad
         *
         * @return Whether this quad is a leaf quad
         */
        public boolean isLeaf(){
            return children == null;
        }
    }
}
