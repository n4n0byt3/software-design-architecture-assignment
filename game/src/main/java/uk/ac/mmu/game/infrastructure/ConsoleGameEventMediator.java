package uk.ac.mmu.game.infrastructure;

import uk.ac.mmu.game.usecase.GameEventMediator;

/**
 * Mediator implementation that logs events to the console.
 */
public class ConsoleGameEventMediator implements GameEventMediator {

    @Override
    public void event(String message) {
        System.out.println("[Mediator] " + message);
    }
}
