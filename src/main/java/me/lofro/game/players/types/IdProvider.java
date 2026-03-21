package me.lofro.game.players.types;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A class designed to manage all ID's generated for the SquidParticipant
 * Dataset.
 *
 */
public class IdProvider {

    private final AtomicInteger currentId;
    private final Stack<Integer> stack;

    public IdProvider() {
        this.currentId = new AtomicInteger(1);
        this.stack = new Stack<>();
    }

    public IdProvider(final int id, final Stack<Integer> stack) {
        this.currentId = new AtomicInteger(id);
        this.stack = stack;
    }

    /**
     * Get the next available ID.
     * 
     * @return The next available ID.
     */
    public int getID() {
        return stack.isEmpty() ? currentId.getAndIncrement() : stack.pop();
    }

    /**
     * Function that adds a new ID to the stack, meant to be called only by PData
     * when a player is removed from dataset.
     * 
     * @param id The ID to add to the stack.
     */
    public void addId(final int id) {
        stack.push(id);
    }

}
