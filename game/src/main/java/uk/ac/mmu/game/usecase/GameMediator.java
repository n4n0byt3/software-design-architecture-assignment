package uk.ac.mmu.game.usecase;

/**
 * Simple Mediator that can be used as a central place to react
 * to high-level game events (start, finish, etc.).
 */
public interface GameMediator {
    void event(String message);
}
