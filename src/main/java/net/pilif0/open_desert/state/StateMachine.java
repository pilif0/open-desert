package net.pilif0.open_desert.state;

import com.sun.istack.internal.NotNull;
import javafx.util.Pair;

import java.util.Map;
import java.util.Set;

/**
 * Represents a finite state machine
 *
 * @author Filip Smola
 * @version 1.0
 */
public class StateMachine<T extends GameState> {
    /** The current state */
    private T current;
    /** All the states of the machine */
    private Set<T> states;
    /** The transitions of the machine */
    private Map<Pair<T, Integer>, T> transitions;



    /**
     * Constructs the state machine from its starting state
     *
     * @param states The states
     * @param start The starting state
     * @param transitions The transitions
     */
    public StateMachine(@NotNull Set<T> states, @NotNull T start, @NotNull Map<Pair<T, Integer>, T> transitions) {
        //Check we start at a valid state
        if(!states.contains(start)){
            throw new IllegalStateException("The start state is not in the states of the machine");
        }

        //Assign members
        this.states = states;
        current = start;
        this.transitions = transitions;

        //Enter the start state
        start.enter();
    }

    /**
     * Moves the state machine along one transition
     *
     * @param i The transition label
     */
    public void go(int i){
        //Try to get the next state
        T next = transitions.get(new Pair<>(current, i));

        //Do nothing if the transition does not exist
        if(next == null){
            return;
        }

        //Move
        current.exit();
        current = next;
        next.enter();
    }

    /**
     * Cleans up each state
     */
    public void cleanUp(){
        current.exit();
        states.forEach(GameState::cleanUp);
    }

    /**
     * Returns the current state
     *
     * @return The current state
     */
    public T getCurrent(){ return current; }

    /**
     * Returns the set of states of this machine
     *
     * @return The set of states of this machine
     */
    public Set<T> getStates(){ return states; }

    /**
     * Returns the transitions of this machine
     *
     * @return The transitions of this machine
     */
    public Map<Pair<T, Integer>, T> getTransitions(){ return transitions; }
}
