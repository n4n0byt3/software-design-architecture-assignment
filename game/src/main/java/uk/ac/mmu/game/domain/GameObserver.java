package uk.ac.mmu.game.domain;

/**
 * Convenience composite observer that listens to all game events.
 * Implements ISP by extending the smaller, cohesive observer interfaces.
 */
public interface GameObserver extends GameStateObserver, PlayerTurnObserver, GameFinishedObserver {
    // no extra methods; just a combination of the three smaller interfaces
}
