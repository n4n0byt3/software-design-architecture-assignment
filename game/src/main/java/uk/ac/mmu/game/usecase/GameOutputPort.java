package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.domain.Board;
import uk.ac.mmu.game.domain.GameObserver;
import uk.ac.mmu.game.domain.MoveResult;
import uk.ac.mmu.game.domain.Player;

/**
 * Output boundary (port) for presenting game information.
 * Also acts as a domain Observer.
 */
public interface GameOutputPort extends GameObserver {

    default void printConfig(Object cfg) { }

    void printTurn(MoveResult result, int turnsForPlayer, Player playerCtx);

    void printWinner(String playerName, int totalTurns, int winnerTurns);

    default void printGameOver() { }

    default void printState(String from, String to) { }

    default void setBoard(Board board) { }
}
