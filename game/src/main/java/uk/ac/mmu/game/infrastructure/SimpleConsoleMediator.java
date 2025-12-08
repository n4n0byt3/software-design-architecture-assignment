package uk.ac.mmu.game.infrastructure;

import uk.ac.mmu.game.usecase.GameMediator;

/**
 * Simple implementation of GameMediator that logs to the console.
 * Demonstrates the Mediator pattern.
 */
public class SimpleConsoleMediator implements GameMediator {

    @Override
    public void event(String message) {
        System.out.println("[Mediator] " + message);
    }
}
