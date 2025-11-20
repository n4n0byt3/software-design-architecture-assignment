package uk.ac.mmu.game.domain;

/**
 * Observer interested only in turns being played.
 */
public interface PlayerTurnObserver {
    void onTurnPlayed(Game game, MoveResult result, Player currentPlayer);
}
