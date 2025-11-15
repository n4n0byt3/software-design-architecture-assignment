package uk.ac.mmu.game.domain;

/**
 * Observer interested only in game completion.
 */
public interface GameFinishedObserver {
    void onGameFinished(Game game, Player winner, int totalTurns, int winnerTurns);
}
