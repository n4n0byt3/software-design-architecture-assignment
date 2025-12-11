package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.domain.Board;
import uk.ac.mmu.game.domain.Game;
import uk.ac.mmu.game.domain.MoveResult;
import uk.ac.mmu.game.domain.Player;
import uk.ac.mmu.game.domain.GameObserver;

/**
 * Output boundary (port) for presenting game information.
 *
 * Acts as:
 * - a Clean Architecture "port" for output (in use case layer)
 * - an Observer of domain events (implements {@link GameObserver})
 *
 * Infrastructure adapters (e.g. {@link uk.ac.mmu.game.infrastructure.ConsoleOutputAdapter})
 * implement this interface to render the game to the console, a file, etc.
 */
public interface OutputPort extends GameObserver {

    /**
     * Called once at the start to supply the configured Board.
     * Allows adapters to format positions correctly.
     */
    default void setBoard(Board board) { }

    /**
     * Print or present a single turn.
     *
     * @param result          immutable move result
     * @param turnsForPlayer  number of turns taken by this player so far
     * @param playerCtx       the player that took the turn
     */
    void printTurn(MoveResult result, int turnsForPlayer, Player playerCtx);

    /**
     * Print or present final winner information and statistics.
     *
     * @param player      winning player name
     * @param totalTurns  total number of turns taken in the game
     * @param winnerTurns number of turns taken by the winner
     */
    void printWinner(String player, int totalTurns, int winnerTurns);

    /**
     * Print configuration or diagnostic metadata.
     * Implementations can safely ignore this if not needed.
     */
    default void printConfig(Object cfg) { }

    /**
     * Called when the Game is in GameOver state and an extra roll
     * is attempted, to demonstrate "Game Over" behaviour.
     */
    default void printGameOver() { }

    /**
     * Print or present state transition information.
     */
    default void printState(String from, String to) { }

    // GameObserver methods are inherited and will typically delegate to
    // printTurn/printWinner/printState/printGameOver from adapters.
}
