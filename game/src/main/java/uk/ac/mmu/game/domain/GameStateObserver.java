package uk.ac.mmu.game.domain;

/**
 * Observer notified whenever the Game changes lifecycle state.
 *
 * This follows the Observer pattern from Week 4:
 * the Subject ({@link Game}) pushes a state-change event to all
 * registered observers, which can then react (e.g. printing to console).
 */
public interface GameStateObserver {

    /**
     * Called when the Game transitions between states.
     *
     * @param game the game whose state has changed
     * @param from previous state name
     * @param to   new state name
     */
    void onStateChanged(Game game, String from, String to);
}
