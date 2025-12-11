package uk.ac.mmu.game.domain;

/**
 * Convenience composite observer that listens to all game events:
 * - state transitions
 * - player turns
 * - game completion
 *
 * This mirrors the multiple-observer pattern in Week 4:
 * a single object can implement all three observer roles at once.
 */
public interface GameObserver extends GameStateObserver, PlayerTurnObserver, GameFinishedObserver {
    // no extra methods
}
