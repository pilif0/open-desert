package net.pilif0.open_desert.components;

import net.pilif0.open_desert.ecs.Component;
import net.pilif0.open_desert.ecs.GameObject;
import net.pilif0.open_desert.ecs.GameObjectEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.util.HashMap;
import java.util.Map;

/**
 * Maintains an up-to-date copy of the world matrix for the game object
 *
 * @author Filip Smola
 * @version 1.0
 */
public class WorldMatrixComponent implements Component {
    /** Name of this component */
    public static final String NAME = "world_matrix";

    /** World matrix */
    private Matrix4f worldMatrix;
    /** Whether the world matrix needs to be recalculated */
    private boolean recalculate;
    /** Component's owner */
    private GameObject owner;

    /**
     * Construct the component with an empty matrix and requiring recalculation
     */
    public WorldMatrixComponent() {
        worldMatrix = new Matrix4f();
        recalculate = true;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void handle(GameObjectEvent e) {
        // Set to recalculate on position, rotation or scale change
        if((e instanceof PositionComponent.PositionEvent) ||
                (e instanceof RotationComponent.RotationEvent) ||
                (e instanceof ScaleComponent.ScaleEvent)){
            recalculate = true;
        }
    }

    @Override
    public Map<String, Object> getState(){
        Map<String, Object> result = new HashMap<>();
        result.put("worldMatrix", worldMatrix);
        result.put("recalculate", recalculate);
        return result;
    }

    @Override
    public void onAttach(GameObject owner) {
        // Remember who the component is attached to and set to recalculate
        this.owner = owner;
        recalculate = true;
    }

    @Override
    public void onDetach(GameObject owner) {
        // Forget the owner
        this.owner = null;
    }

    @Override
    public void overrideFields(Map<String, Object> overrides) {
        // Nothing in this component is to be overridden, the fields start with functional values
    }

    /**
     * Return the world matrix
     *
     * @return World matrix
     */
    public Matrix4fc getWorldMatrix(){
        if(recalculate){
            worldMatrix.identity();
            if(owner.position != null){
                worldMatrix.translate(owner.position.getPosition3D());
            }
            if(owner.rotation != null) {
                worldMatrix.rotateZ(owner.rotation.getRotation());
            }
            if(owner.scale != null) {
                worldMatrix.scale(owner.scale.getScale3D());
            }
        }

        return worldMatrix.toImmutable();
    }
}
