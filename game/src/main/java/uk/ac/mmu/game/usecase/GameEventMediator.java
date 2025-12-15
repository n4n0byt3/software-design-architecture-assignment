package uk.ac.mmu.game.usecase;

/**
 * Simple mediator that can be used as a central place to react to high-level events
 * (start, finish, etc.). In this project the infrastructure implementation logs to console.
 */
public interface GameEventMediator {
    void event(String message);
}
