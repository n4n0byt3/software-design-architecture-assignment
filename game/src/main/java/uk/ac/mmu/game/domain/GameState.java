package uk.ac.mmu.game.domain;

public interface GameState {
    /** Human-readable name for printing transitions. */
    String name();

    /** Called when the state becomes active. */
    default void enter(Game game) { }

    /** Perform one logical turn in this state. */
    MoveResult playTurn(Game game);
}
