package uk.ac.mmu.game.usecase;

/**
 * Simple Mediator for high-level game lifecycle events.
 *
 * Unlike the fine-grained Observer interfaces in the domain layer,
 * this abstraction is used at the use case / application level
 * for cross-cutting concerns such as logging or orchestration.
 */
public interface GameMediator {

    /**
     * Handle a generic high-level event message.
     *
     * @param message human-readable event description
     */
    void event(String message);
}
