package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.domain.MoveResult;
import uk.ac.mmu.game.domain.Player;

public interface OutputPort {
    default void printConfig(Object cfg) {}
    void printTurn(MoveResult result, int turnsForPlayer, Player playerCtx);
    void printWinner(String playerName, int totalTurns, int winnerTurns);
    default void printGameOver() {}
    default void printState(String from, String to) {}
}
