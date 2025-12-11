package uk.ac.mmu.game.domain;

/**
 * Observer notified when the Game reaches a terminal "GameOver" state
 * and a winner (if any) has been determined.
 *
 * Typical responsibilities for implementations:
 * - print final statistics
 * - log outcomes
 * - trigger persistence or external notifications
 */
public interface GameFinishedObserver {

    /**
     * Called exactly once when the Game has finished.
     *
     * @param game        the finished game
     * @param winner      winning player, or null if no winner
     * @param totalTurns  total number of turns taken by all players
     * @param winnerTurns number of turns taken by the winning player
     */
    void onGameFinished(Game game, Player winner, int totalTurns, int winnerTurns);
}
