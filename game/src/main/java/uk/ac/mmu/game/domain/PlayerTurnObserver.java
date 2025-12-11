package uk.ac.mmu.game.domain;

/**
 * Observer notified whenever a player completes a turn.
 *
 * Follows the "push" model from Week 4:
 * the Subject ({@link Game}) pushes an immutable {@link MoveResult}
 * together with the current player so observers do not need to query
 * the Game for additional context.
 */
public interface PlayerTurnObserver {

    /**
     * Called after a single logical turn has been played.
     *
     * @param game          the game being played
     * @param result        immutable snapshot of the move result
     * @param currentPlayer the player who took this turn
     */
    void onTurnPlayed(Game game, MoveResult result, Player currentPlayer);
}
