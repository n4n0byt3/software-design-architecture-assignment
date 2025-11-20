package uk.ac.mmu.game.domain;

/**
 * Observer interested only in game state transitions.
 */
public interface GameStateObserver {
    void onStateChanged(Game game, String from, String to);
}
