package uk.ac.mmu.game.usecase;

/**
 * Simple Mediator that can be used as a central place to react
 * to high-level game events (start, finish, etc.).
 *
 * For this assignment we keep it small, but it demonstrates
 * the Mediator pattern discussed in Week 6.
 */
public interface GameMediator {
    void event(String message);
}
