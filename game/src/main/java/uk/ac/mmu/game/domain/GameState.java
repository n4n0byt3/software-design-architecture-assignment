package uk.ac.mmu.game.domain;

/**
 * State pattern interface for the game lifecycle.
 */
public interface GameState {

    String name();

    default void enter(Game game) {
        // Optional hook
    }

    MoveResult playTurn(Game game);
}
