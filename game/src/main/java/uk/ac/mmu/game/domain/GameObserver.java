package uk.ac.mmu.game.domain;

/**
 * Convenience composite observer that listens to all game events.
 */
public interface GameObserver extends GameStateObserver, PlayerTurnObserver, GameFinishedObserver {
    // no extra methods
}
