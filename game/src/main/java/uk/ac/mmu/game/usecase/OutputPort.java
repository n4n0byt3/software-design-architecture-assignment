package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.domain.Board;
import uk.ac.mmu.game.domain.GameObserver;
import uk.ac.mmu.game.domain.MoveResult;
import uk.ac.mmu.game.domain.Player;

/**
 * Output boundary (port) for presenting game information.
 *
 * This interface:
 *  - Acts as a port in the Ports & Adapters / Clean Architecture style.
 *  - Extends {@link GameObserver}, so a single object can both
 *    observe domain events and decide how to present them.
 *  - Demonstrates the Dependency Inversion Principle: the
 *    high-level use case depends only on this abstraction.
 */
public interface OutputPort extends GameObserver {

    /**
     * Optional: print configuration details for the current run.
     */
    default void printConfig(Object cfg) { }

    /**
     * Present a turn result in a human-readable way.
     * Typically called from the observer callback, not directly
     * by the use case.
     */
    void printTurn(MoveResult result, int turnsForPlayer, Player playerCtx);

    /**
     * Present the final winner summary.
     * Typically called from the observer callback, not directly
     * by the use case.
     */
    void printWinner(String playerName, int totalTurns, int winnerTurns);

    /**
     * Optional: notify that the game is over without a winner.
     */
    default void printGameOver() { }

    /**
     * Optional: notify that the game lifecycle state has changed.
     */
    default void printState(String from, String to) { }

    /**
     * Provide the board so the presenter can render friendly labels.
     * This keeps the domain model out of the CLI runner and confines
     * presentation logic to the adapter.
     */
    default void setBoard(Board board) { }
}
